package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.api.pojo.bo.HostResourceInfoBO;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.manage.service.ResourceAllocationService;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.center.support.websocket.WsMessageHolder;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.constants.VmwareRegulateConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.enums.MachineTypeEnum;
import org.travis.shared.common.enums.MsgModuleEnum;
import org.travis.shared.common.enums.MsgStateEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.LockConflictException;
import org.travis.shared.common.exceptions.NotFoundException;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ResourceAllocationServiceImpl
 * @Description ResourceAllocationServiceImpl
 * @Author Travis
 * @Data 2024/10
 */
@Slf4j
@Service
public class ResourceAllocationServiceImpl implements ResourceAllocationService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private VmwareInfoMapper vmwareInfoMapper;
    @Resource
    private HostInfoMapper hostInfoMapper;
    @Resource
    private VmwareInfoService vmwareInfoService;
    @Resource
    private WsMessageHolder wsMessageHolder;
    @DubboReference
    private AgentHostClient agentHostClient;

    @Transactional
    @Override
    public R<?> expandCpuResource(String vmwareUuid, Boolean autoFlag) {
        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareUuid);
        try {
            // 0.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(1, TimeUnit.SECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 1.校验近期是否触发过自动调控
            if (autoFlag) {
                RBucket<String> bucket = redissonClient.getBucket(VmwareRegulateConstant.UPDATE_CPU_KEY_PREFIX + vmwareUuid);
                Assert.isTrue(bucket.setIfAbsent(vmwareUuid, Duration.ofMinutes(3)), () -> new CommonException(BizCodeEnum.TOO_MANY_REQUESTS.getCode(), "近期已触发过「CPU」自动调控, 稍后再试!"));
            }

            // 2.查询虚拟机相关信息
            VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, vmwareUuid))).orElseThrow(() -> new NotFoundException("未找到虚拟机相关信息"));
            HostInfo hostInfo = Optional.ofNullable(hostInfoMapper.selectOne(Wrappers.<HostInfo>lambdaQuery().eq(HostInfo::getId, vmwareInfo.getHostId()))).orElseThrow(() -> new NotFoundException("未查询到虚拟机所在宿主机信息!"));

            // 3.查询虚拟机所在宿主机实时资源状态
            R<HostResourceInfoBO> hostResourceInfoBOR = agentHostClient.queryHostResourceInfo(hostInfo.getIp());
            if (hostResourceInfoBOR.checkFail()) {
                throw new CommonException(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), "宿主机实时资源状态查询失败!");
            }
            HostResourceInfoBO hostResourceInfoBO = hostResourceInfoBOR.getData();

            // 4.校验当前宿主机是否满足扩容需求
            R<Void> expandResult = null;
            if (vmwareInfo.getVcpuCurrent() <= (hostResourceInfoBO.getVCpuAllNum() - hostResourceInfoBO.getVCpuDefinitionNum())) {
                // 4.1.资源满足: CPU 扩容
                expandResult = execVcpuSatisfyCase(vmwareInfo.getId(), vmwareInfo.getVcpuCurrent() * 2);
            } else {
                // 4.2.资源不满足: 查询满足 CPU 扩容需求 & 健康分数最高的宿主机
                expandResult = execVcpuDissatisfyCase();
            }

            // 5.推送全局消息
            if (expandResult.checkSuccess()) {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机CPU{}扩容", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.INFO)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」扩容成功!", vmwareInfo.getName()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            } else {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机CPU{}扩容", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.ERROR)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」扩容失败:{}", vmwareInfo.getName(), expandResult.getMsg()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            }
            return expandResult;
        } catch (CommonException commonException) {
            log.error("expandCpuResource common error: {}", commonException.getMessage());
            throw commonException;
        } catch (Exception e) {
            log.error("expandCpuResource unknow error: {}", e.getMessage());
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private R<Void> execVcpuDissatisfyCase() {
        try {
            // TODO 查询满足 CPU 扩容需求 & 健康分数最高的宿主机

            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    private R<Void> execVcpuSatisfyCase(Long vmwareId, int targetVcpuNumber) {
        try {
            vmwareInfoService.modifyVmwareVcpuNumber(vmwareId, targetVcpuNumber);
            return R.ok();
        } catch (CommonException e) {
            return R.error(e.getCode(), e.getMessage());
        }
    }

    @Override
    public R<?> expandMemoryResource(String vmwareUuid, Boolean autoFlag) {
        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareUuid);
        try {
            // 0.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(1, TimeUnit.SECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 1.校验近期是否触发过自动调控
            if (autoFlag) {
                RBucket<String> bucket = redissonClient.getBucket(VmwareRegulateConstant.UPDATE_MEM_KEY_PREFIX + vmwareUuid);
                Assert.isTrue(bucket.setIfAbsent(vmwareUuid, Duration.ofMinutes(3)), () -> new CommonException(BizCodeEnum.TOO_MANY_REQUESTS.getCode(), "近期已触发过「MEM」自动调控, 稍后再试!"));
            }

            // 2.查询虚拟机相关信息
            VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, vmwareUuid))).orElseThrow(() -> new NotFoundException("未找到虚拟机相关信息"));
            HostInfo hostInfo = Optional.ofNullable(hostInfoMapper.selectOne(Wrappers.<HostInfo>lambdaQuery().eq(HostInfo::getId, vmwareInfo.getHostId()))).orElseThrow(() -> new NotFoundException("未查询到虚拟机所在宿主机信息!"));

            // 3.查询虚拟机所在宿主机实时资源状态
            R<HostResourceInfoBO> hostResourceInfoBOR = agentHostClient.queryHostResourceInfo(hostInfo.getIp());
            if (hostResourceInfoBOR.checkFail()) {
                throw new CommonException(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), "宿主机实时资源状态查询失败!");
            }
            HostResourceInfoBO hostResourceInfoBO = hostResourceInfoBOR.getData();

            // 4.校验当前宿主机是否满足扩容需求
            R<Void> expandResult = null;
            if (isMemorySatisfy(vmwareInfo, hostResourceInfoBO)) {
                // 4.1.满足扩容需求: 内存扩容
                expandResult = execMemorySatisfyCase(vmwareInfo.getId(), vmwareInfo.getMemoryCurrent() * 2);
            } else {
                // 4.2.不满足扩容需求: 查询满足内存扩容需求 & 健康分数最高的宿主机
                expandResult = execMemoryDissatisfyCase();
            }

            // 5.推送全局消息
            if (expandResult.checkSuccess()) {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机内存{}扩容", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.INFO)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」扩容成功!", vmwareInfo.getName()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            } else {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机内存{}扩容", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.ERROR)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」扩容失败:{}", vmwareInfo.getName(), expandResult.getMsg()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            }
            return expandResult;
        } catch (CommonException commonException) {
            log.error("expandMemoryResource common error: {}", commonException.getMessage());
            throw commonException;
        } catch (Exception e) {
            log.error("expandMemoryResource unknow error: {}", e.getMessage());
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    private boolean isMemorySatisfy(VmwareInfo vmwareInfo, HostResourceInfoBO hostResourceInfoBO) {
        if (VmwareStateEnum.SHUT_OFF.equals(vmwareInfo.getState())) {
            return vmwareInfo.getMemoryCurrent() * 2 < (hostResourceInfoBO.getMemoryTotalMax() - hostResourceInfoBO.getMemoryTotalInUse()) * 0.95;
        } else {
            return vmwareInfo.getMemoryCurrent() < (hostResourceInfoBO.getMemoryTotalMax() - hostResourceInfoBO.getMemoryTotalInUse()) * 0.95;
        }
    }

    private R<Void> execMemoryDissatisfyCase() {
        try {
            // TODO 查询满足内存扩容需求 & 健康分数最高的宿主机

            return R.ok();
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    private R<Void> execMemorySatisfyCase(Long vmwareId, long targetMemorySize) {
        try {
            vmwareInfoService.modifyVmwareMemory(vmwareId, targetMemorySize);
            return R.ok();
        } catch (CommonException e) {
            return R.error(e.getCode(), e.getMessage());
        }
    }

    @Override
    public R<?> reduceCpuResource(String vmwareUuid, Boolean autoFlag) {
        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareUuid);
        try {
            // 0.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(1, TimeUnit.SECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 1.校验近期是否触发过自动调控
            if (autoFlag) {
                RBucket<String> bucket = redissonClient.getBucket(VmwareRegulateConstant.UPDATE_CPU_KEY_PREFIX + vmwareUuid);
                Assert.isTrue(bucket.setIfAbsent(vmwareUuid, Duration.ofMinutes(3)), () -> new CommonException(BizCodeEnum.TOO_MANY_REQUESTS.getCode(), "近期已触发过「CPU」自动调控, 稍后再试!"));
            }

            // 2.查询虚拟机相关信息
            VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, vmwareUuid))).orElseThrow(() -> new NotFoundException("未找到虚拟机相关信息"));

            // 3.1.校验是否可执行 CPU 缩减
            if (vmwareInfo.getVcpuCurrent() < 2) {
                log.warn("UUID:{}, 虚拟机CPU已达下限, 不支持缩减!", vmwareUuid);
                return R.ok();
            }

            // 3.2.执行 CPU 缩减
            R<Void> expandResult = execVcpuSatisfyCase(vmwareInfo.getId(), (int) Math.ceil(vmwareInfo.getVcpuCurrent() / 2.0));

            // 4.推送全局消息
            if (expandResult.checkSuccess()) {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机CPU{}缩减", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.INFO)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」缩减成功!", vmwareInfo.getName()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            } else {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机CPU{}缩减", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.ERROR)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」缩减失败:{}", vmwareInfo.getName(), expandResult.getMsg()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            }
            return expandResult;
        } catch (CommonException commonException) {
            log.error("reduceCpuResource common error: {}", commonException.getMessage());
            throw commonException;
        } catch (Exception e) {
            log.error("reduceCpuResource unknow error: {}", e.getMessage());
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    @Override
    public R<?> reduceMemoryResource(String vmwareUuid, Boolean autoFlag) {
        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareUuid);
        try {
            // 0.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(1, TimeUnit.SECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 1.校验近期是否触发过自动调控
            if (autoFlag) {
                RBucket<String> bucket = redissonClient.getBucket(VmwareRegulateConstant.UPDATE_MEM_KEY_PREFIX + vmwareUuid);
                Assert.isTrue(bucket.setIfAbsent(vmwareUuid, Duration.ofMinutes(3)), () -> new CommonException(BizCodeEnum.TOO_MANY_REQUESTS.getCode(), "近期已触发过「MEM」自动调控, 稍后再试!"));
            }

            // 2.查询虚拟机相关信息
            VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, vmwareUuid))).orElseThrow(() -> new NotFoundException("未找到虚拟机相关信息"));

            // 3.1.校验是否可执行 MEM 缩减
            if (vmwareInfo.getMemoryCurrent() <= 2 * SystemConstant.GB_UNIT) {
                log.warn("UUID:{}, 虚拟机内存已达下限, 不支持缩减!", vmwareUuid);
                return R.ok();
            }

            // 3.2.校验当前宿主机是否满足扩容需求
            R<Void> expandResult = execMemorySatisfyCase(vmwareInfo.getId(), vmwareInfo.getMemoryCurrent() / 2);

            // 4.推送全局消息
            if (expandResult.checkSuccess()) {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机内存{}缩减", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.INFO)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」缩减成功!", vmwareInfo.getName()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            } else {
                wsMessageHolder.sendGlobalMessage(
                        WebSocketMessage.builder()
                                .msgTitle(StrUtil.format("虚拟机内存{}缩减", autoFlag ? "自动" : "手动"))
                                .msgModule(MsgModuleEnum.VMWARE)
                                .msgState(MsgStateEnum.ERROR)
                                .msgContent(StrUtil.format("{} -> 虚拟机「CPU」缩减失败:{}", vmwareInfo.getName(), expandResult.getMsg()))
                                .nodeMachineType(MachineTypeEnum.VMWARE)
                                .nodeMachineUuid(vmwareUuid)
                                .build()
                );
            }
            return expandResult;
        } catch (CommonException commonException) {
            log.error("reduceMemoryResource common error: {}", commonException.getMessage());
            throw commonException;
        } catch (Exception e) {
            log.error("reduceMemoryResource unknow error: {}", e.getMessage());
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    @Override
    public R<?> recommendCpuResource(String vmwareUuid, boolean expandFlag) {
        // 1.校验近期是否触发过资源推荐
        RBucket<String> bucket = redissonClient.getBucket(VmwareRegulateConstant.RECOMMEND_CPU_KEY_PREFIX + vmwareUuid);
        Assert.isTrue(bucket.setIfAbsent(vmwareUuid, Duration.ofMinutes(10)), () -> new CommonException(BizCodeEnum.TOO_MANY_REQUESTS.getCode(), "近期已触发过「CPU」资源推荐!"));

        // 2.查询虚拟机及宿主机信息
        VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, vmwareUuid))).orElseThrow(() -> new NotFoundException("未找到虚拟机相关信息"));

        // 3.资源推荐信息封装
        WebSocketMessage message = null;
        if (expandFlag) {
            message = WebSocketMessage.builder()
                    .msgTitle(StrUtil.format("虚拟机CPU扩容推荐"))
                    .msgModule(MsgModuleEnum.VMWARE)
                    .msgState(MsgStateEnum.INFO)
                    .msgContent(StrUtil.format("虚拟机 UUID: {}, 目前 CPU 核数: {}, 推荐扩容为: {} 核!", vmwareUuid, vmwareInfo.getVcpuCurrent(), vmwareInfo.getVcpuCurrent() * 2))
                    .nodeMachineType(MachineTypeEnum.VMWARE)
                    .nodeMachineUuid(vmwareUuid)
                    .build();
        } else {
            if (vmwareInfo.getVcpuCurrent() < 2) {
                log.warn("UUID:{}, 虚拟机 CPU 已达下限, 无需推荐缩减!", vmwareUuid);
                return R.ok();
            }
            message = WebSocketMessage.builder()
                    .msgTitle(StrUtil.format("虚拟机CPU缩减推荐"))
                    .msgModule(MsgModuleEnum.VMWARE)
                    .msgState(MsgStateEnum.INFO)
                    .msgContent(StrUtil.format("虚拟机 UUID: {}, 目前 CPU 核数: {}, 推荐缩减为: {} 核!", vmwareUuid, vmwareInfo.getVcpuCurrent(), vmwareInfo.getVcpuCurrent() / 2))
                    .nodeMachineType(MachineTypeEnum.VMWARE)
                    .nodeMachineUuid(vmwareUuid)
                    .build();
        }
        wsMessageHolder.sendGlobalMessage(message);
        return R.ok();
    }

    @Override
    public R<?> recommendMemoryResource(String vmwareUuid, boolean expandFlag) {
        // 1.校验近期是否触发过资源推荐
        RBucket<String> bucket = redissonClient.getBucket(VmwareRegulateConstant.RECOMMEND_MEM_KEY_PREFIX + vmwareUuid);
        Assert.isTrue(bucket.setIfAbsent(vmwareUuid, Duration.ofMinutes(10)), () -> new CommonException(BizCodeEnum.TOO_MANY_REQUESTS.getCode(), "近期已触发过「MEM」资源推荐!"));

        // 2.查询虚拟机及宿主机信息
        VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, vmwareUuid))).orElseThrow(() -> new NotFoundException("未找到虚拟机相关信息"));

        // 3.资源推荐信息封装
        WebSocketMessage message = null;
        if (expandFlag) {
            message = WebSocketMessage.builder()
                    .msgTitle(StrUtil.format("虚拟机MEM扩容推荐"))
                    .msgModule(MsgModuleEnum.VMWARE)
                    .msgState(MsgStateEnum.INFO)
                    .msgContent(StrUtil.format("虚拟机 UUID: {}, 目前内存大小: {} MB, 推荐扩容为: {} MB!", vmwareUuid, vmwareInfo.getMemoryCurrent() / SystemConstant.MB_UNIT, vmwareInfo.getMemoryCurrent() / SystemConstant.MB_UNIT * 2))
                    .nodeMachineType(MachineTypeEnum.VMWARE)
                    .nodeMachineUuid(vmwareUuid)
                    .build();
        } else {
            if (vmwareInfo.getMemoryCurrent() < 2 * SystemConstant.GB_UNIT) {
                log.warn("UUID:{}, 虚拟机 MEM 已达下限, 无需推荐缩减!", vmwareUuid);
                return R.ok();
            }
            message = WebSocketMessage.builder()
                    .msgTitle(StrUtil.format("虚拟机MEM缩减推荐"))
                    .msgModule(MsgModuleEnum.VMWARE)
                    .msgState(MsgStateEnum.INFO)
                    .msgContent(StrUtil.format("虚拟机 UUID: {}, 目前内存大小: {} MB, 推荐缩减为: {} MB!", vmwareUuid, vmwareInfo.getMemoryCurrent() / SystemConstant.MB_UNIT, vmwareInfo.getMemoryCurrent() / SystemConstant.MB_UNIT / 2))
                    .nodeMachineType(MachineTypeEnum.VMWARE)
                    .nodeMachineUuid(vmwareUuid)
                    .build();
        }
        wsMessageHolder.sendGlobalMessage(message);
        return R.ok();
    }
}
