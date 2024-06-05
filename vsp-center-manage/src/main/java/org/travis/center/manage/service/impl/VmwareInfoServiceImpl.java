package org.travis.center.manage.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.api.pojo.bo.HostResourceInfoBO;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.shared.common.enums.MsgModuleEnum;
import org.travis.shared.common.enums.MsgStateEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.utils.ManageThreadPoolConfig;
import org.travis.center.manage.creation.AbstractCreationService;
import org.travis.center.manage.creation.CreationHolder;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.center.support.websocket.WsMessageHolder;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.*;

import javax.annotation.Resource;

/**
 * @ClassName VmwareInfoServiceImpl
 * @Description VmwareInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Slf4j
@Service
public class VmwareInfoServiceImpl extends ServiceImpl<VmwareInfoMapper, VmwareInfo> implements VmwareInfoService{

    @Resource
    private CreationHolder creationHolder;
    @Resource
    private HostInfoMapper hostInfoMapper;
    @DubboReference
    public AgentVmwareClient agentVmwareClient;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private WsMessageHolder wsMessageHolder;
    @Resource
    private AgentHostClient agentHostClient;

    @Override
    public VmwareInfo selectOne(Long id) {
        Optional<VmwareInfo> vmwareInfoOptional = Optional.ofNullable(getById(id));
        Assert.isTrue(vmwareInfoOptional.isPresent(), () -> new BadRequestException("未查询到相关虚拟机信息!"));
        return vmwareInfoOptional.get();
    }

    @Override
    public List<VmwareInfo> selectAll() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<VmwareInfo> pageSelectList(PageQuery pageQuery) {
        Page<VmwareInfo> vmwareInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(vmwareInfoPage);
    }

    @Override
    public void createVmwareInfo(VmwareInsertDTO vmwareInsertDTO) {
        // TODO 添加虚拟机与权限组关联关系
        // 获取虚拟机创建持有者
        AbstractCreationService creationService = creationHolder.getCreationService(vmwareInsertDTO.getCreateForm().getValue());
        // 异步创建虚拟机
        CompletableFuture.runAsync(() -> {
                    try {
                        creationService.build(vmwareInsertDTO);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, ManageThreadPoolConfig.businessProcessExecutor)
                .thenRun(() -> {
                    // 全局推送创建成功消息,记录日志
                    wsMessageHolder.sendGlobalMessage(
                            WebSocketMessage.builder()
                                    .msgModule(MsgModuleEnum.VMWARE)
                                    .msgState(MsgStateEnum.INFO)
                                    .msgContent(StrUtil.format("{} -> 虚拟机异步创建成功!", vmwareInsertDTO.getName()))
                                    .build()
                    );
                })
                .exceptionally(ex -> {
                    // 全局推送创建失败消息,记录日志
                    wsMessageHolder.sendGlobalMessage(
                            WebSocketMessage.builder()
                                    .msgModule(MsgModuleEnum.VMWARE)
                                    .msgState(MsgStateEnum.ERROR)
                                    .msgContent(StrUtil.format("{} -> 虚拟机异步创建失败:{}", vmwareInsertDTO.getName(), ex))
                                    .build()
                    );
                    return null;
                });


        wsMessageHolder.sendGlobalMessage(
                WebSocketMessage.builder()
                        .msgModule(MsgModuleEnum.VMWARE)
                        .msgState(MsgStateEnum.INFO)
                        .msgContent(StrUtil.format("{} -> 虚拟机异步创建中, 请关注全局消息!", vmwareInsertDTO.getName()))
                        .build()
        );
    }

    @Override
    public List<VmwareErrorVO> startVmware(List<Long> vmwareIds) {
        // 1.1.校验列表
        Assert.isTrue(CollectionUtil.isNotEmpty(vmwareIds), () -> new BadRequestException("列表为空!"));
        // 1.2.初始化错误列表 (返回结果)
        List<VmwareErrorVO> vmwareErrorList = new ArrayList<>();

        // 2.启动前宿主机 CPU、内存资源校验
        List<VmwareInfo> vmwareInfoList = getBaseMapper().selectList(Wrappers.<VmwareInfo>lambdaQuery().in(VmwareInfo::getId, vmwareIds));

        // 3.遍历虚拟机列表，逐一启动
        for (VmwareInfo vmwareInfo : vmwareInfoList) {
            VmwareErrorVO vmwareErrorVO = startSingleVmware(vmwareInfo);
            if (vmwareErrorVO != null) {
                vmwareErrorList.add(vmwareErrorVO);
            }
        }
        return vmwareErrorList;
    }

    private VmwareErrorVO startSingleVmware(VmwareInfo vmwareInfo) {
        VmwareErrorVO vmwareErrorVO = null;
        RLock hostLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareInfo.getId());
        RLock vmLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareInfo.getHostId());
        try {
            // 1.1.根据宿主机 ID 加锁, 尝试拿锁
            Assert.isTrue(hostLock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException("宿主机正在操作中，请稍后重试!"));
            // 1.2.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(vmLock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 2.查询虚拟机所属宿主机IP信息
            HostInfo hostInfo = queryHostInfoByVmwareInfo(vmwareInfo);
            String hostIp = hostInfo.getIp();

            // 3.Dubbo 获取宿主机实时资源信息
            R<HostResourceInfoBO> hostResourceInfoBOR = agentHostClient.queryHostResourceInfo(hostIp);
            Assert.isTrue(hostResourceInfoBOR.checkSuccess(), () -> new NotFoundException("宿主机实时资源信息查询失败!"));

            HostResourceInfoBO resourceInfoBO = hostResourceInfoBOR.getData();
            int remainingVcpuNumber = resourceInfoBO.getVCpuAllNum() - resourceInfoBO.getVCpuActiveNum();
            long remainingMemorySize = resourceInfoBO.getMemoryTotalMax() - resourceInfoBO.getMemoryTotalInUse();

            // 4.校验虚拟机所需内存和虚拟CPU
            if (vmwareInfo.getMemoryCurrent() > remainingMemorySize || vmwareInfo.getVcpuCurrent() > remainingVcpuNumber) {
                throw new CommonException(BizCodeEnum.HOST_RESOURCE_LACK.getCode(), BizCodeEnum.HOST_RESOURCE_LACK.getMessage());
            }

            // 5.预先修改状态为：启动中
            getBaseMapper().update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, VmwareStateEnum.STARTING).eq(VmwareInfo::getId, vmwareInfo.getId()));

            // 6.虚拟机启动
            R<String> startedVmwareR = agentVmwareClient.startVmware(hostIp, vmwareInfo.getUuid());
            Assert.isTrue(startedVmwareR.checkSuccess(), () -> new DubboFunctionException("虚拟机启动失败:" + startedVmwareR.getMsg()));

            // 7.修改虚拟机状态为：运行状态
            getBaseMapper().update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, VmwareStateEnum.POWER_ON).eq(VmwareInfo::getId, vmwareInfo.getId()));
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 启动失败则封装错误消息
            vmwareErrorVO = new VmwareErrorVO();
            vmwareErrorVO.setVmwareId(vmwareInfo.getId());
            vmwareErrorVO.setErrorMessage(e.getMessage());
            return vmwareErrorVO;
        } finally {
            if (vmLock.isHeldByCurrentThread()) {
                vmLock.unlock();
            }
            if (hostLock.isHeldByCurrentThread()) {
                hostLock.unlock();
            }
        }
    }

    @Override
    public List<VmwareErrorVO> suspendVmware(List<Long> vmwareIds) {
        // 1.校验列表
        Assert.isTrue(CollectionUtil.isNotEmpty(vmwareIds), () -> new BadRequestException("列表为空!"));

        return commonVmwareOperation(
                vmwareIds,
                VmwareStateEnum.PAUSE,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.suspendVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    @Override
    public List<VmwareErrorVO> resumeVmware(List<Long> vmwareIds) {
        // 1.校验列表
        Assert.isTrue(CollectionUtil.isNotEmpty(vmwareIds), () -> new BadRequestException("列表为空!"));

        return commonVmwareOperation(
                vmwareIds,
                VmwareStateEnum.POWER_ON,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.resumeVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    @Override
    public List<VmwareErrorVO> shutdownVmware(List<Long> vmwareIds) {
        // 1.校验列表
        Assert.isTrue(CollectionUtil.isNotEmpty(vmwareIds), () -> new BadRequestException("列表为空!"));

        return commonVmwareOperation(
                vmwareIds,
                VmwareStateEnum.POWER_OFF,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.shutdownVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    @Override
    public List<VmwareErrorVO> destroyVmware(List<Long> vmwareIds) {
        // 1.校验列表
        Assert.isTrue(CollectionUtil.isNotEmpty(vmwareIds), () -> new BadRequestException("列表为空!"));

        return commonVmwareOperation(
                vmwareIds,
                VmwareStateEnum.POWER_OFF,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.destroyVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    @Override
    public List<VmwareErrorVO> deleteVmware(List<Long> vmwareIds) {
        // 1.校验列表
        Assert.isTrue(CollectionUtil.isNotEmpty(vmwareIds), () -> new BadRequestException("列表为空!"));

        return deleteVmwareOperation(
                vmwareIds,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.undefineVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    @Override
    public void modifyVmwareMemory(Long vmwareId, Long memory) {
        RLock lock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareId);
        try {
            // 0.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(lock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException(BizCodeEnum.LOCKED.getCode(), "虚拟机正在操作中，请稍后重试!"));

            // 1.查询虚拟机所在物理机信息
            VmwareInfo vmwareInfo = getBaseMapper().selectOne(Wrappers.<VmwareInfo>lambdaQuery().select(VmwareInfo::getHostId).eq(VmwareInfo::getId, vmwareId));
            Assert.notNull(vmwareInfo, () -> new NotFoundException("未查询到虚拟机信息!"));
            HostInfo hostInfo = queryHostInfoByVmwareInfo(vmwareInfo);

            // 2.1.统一状态校验：校验是否超过最大内存
            Assert.isTrue(memory <= vmwareInfo.getMemoryMax(), () -> new BadRequestException("超出虚拟机最大内存限制:" + vmwareInfo.getMemoryMax()));

            // 2.2.非关机状态 & 资源扩展 -> 资源校验
            if (!VmwareStateEnum.POWER_OFF.equals(vmwareInfo.getState()) && memory > vmwareInfo.getMemoryCurrent()) {
                // Dubbo 获取宿主机实时资源信息
                R<HostResourceInfoBO> hostResourceInfoBOR = agentHostClient.queryHostResourceInfo(hostInfo.getIp());
                Assert.isTrue(hostResourceInfoBOR.checkSuccess(), () -> new NotFoundException("宿主机实时资源信息查询失败!"));
                // 校验宿主机剩余内存的 95% 是否满足虚拟机资源需求
                HostResourceInfoBO resourceInfoBO = hostResourceInfoBOR.getData();
                Assert.isTrue(memory - vmwareInfo.getMemoryCurrent() <= (resourceInfoBO.getMemoryTotalMax() - resourceInfoBO.getMemoryTotalInUse()) * 0.95, () -> new BadRequestException("虚拟机内存容量已超出宿主机剩余内存, 请更换宿主机或调整虚拟机内存容量! 宿主机剩余内存: " + (resourceInfoBO.getMemoryTotalMax() - resourceInfoBO.getMemoryTotalInUse())));
            }

            // 3.获取宿主机 IP 信息，并发送 Dubbo 消息
            R<Void> modifiedR = agentVmwareClient.modifyVmwareMemory(hostInfo.getIp(), vmwareInfo.getUuid(), memory, VmwareStateEnum.POWER_OFF.equals(vmwareInfo.getState()));
            Assert.isTrue(modifiedR.checkSuccess(), () -> new DubboFunctionException("虚拟机内存大小修改失败:" + modifiedR.getMsg()));

            // 4.修改数据库内存大小
            getBaseMapper().update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getMemoryCurrent, memory).eq(VmwareInfo::getId, vmwareId));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void modifyVmwareVcpuNumber(Long vmwareId, Integer vcpuNumber) {
        RLock lock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareId);
        try {
            // 0.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(lock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 1.查询虚拟机所在物理机信息
            VmwareInfo vmwareInfo = getBaseMapper().selectOne(Wrappers.<VmwareInfo>lambdaQuery().select(VmwareInfo::getHostId).eq(VmwareInfo::getId, vmwareId));
            Assert.notNull(vmwareInfo, () -> new NotFoundException("未查询到虚拟机信息!"));
            HostInfo hostInfo = queryHostInfoByVmwareInfo(vmwareInfo);

            // 2.1.统一状态校验：校验是否超过最大 VCPU 数量
            Assert.isTrue(vcpuNumber <= vmwareInfo.getVcpuMax(), () -> new BadRequestException("超出虚拟机最大CPU数量限制:" + vmwareInfo.getVcpuMax()));

            // 2.2.非关机状态 & 资源扩展 -> 资源校验
            if (!VmwareStateEnum.POWER_OFF.equals(vmwareInfo.getState()) && vcpuNumber > vmwareInfo.getVcpuCurrent()) {
                // Dubbo 获取宿主机实时资源信息
                R<HostResourceInfoBO> hostResourceInfoBOR = agentHostClient.queryHostResourceInfo(hostInfo.getIp());
                Assert.isTrue(hostResourceInfoBOR.checkSuccess(), () -> new NotFoundException("宿主机实时资源信息查询失败!"));
                // 校验宿主机 VCPU 剩余数量是否满足虚拟机资源需求
                HostResourceInfoBO resourceInfoBO = hostResourceInfoBOR.getData();
                Assert.isTrue(vcpuNumber - vmwareInfo.getVcpuCurrent() <= resourceInfoBO.getVCpuAllNum() - resourceInfoBO.getVCpuActiveNum(), () -> new BadRequestException("虚拟机CPU容量已超出宿主机剩余虚拟CPU, 请更换宿主机或调整虚拟机CPU容量! 宿主机剩余CPU: " + (resourceInfoBO.getVCpuAllNum() - resourceInfoBO.getVCpuActiveNum())));
            }

            // 3.获取宿主机 IP 信息，并发送 Dubbo 消息
            R<Void> modifiedR = agentVmwareClient.modifyVmwareVcpu(hostInfo.getIp(), vmwareInfo.getUuid(), vcpuNumber, VmwareStateEnum.POWER_OFF.equals(vmwareInfo.getState()));
            Assert.isTrue(modifiedR.checkSuccess(), () -> new DubboFunctionException("虚拟机虚拟CPU数量修改失败:" + modifiedR.getMsg()));

            // 4.修改数据库
            getBaseMapper().update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getVcpuCurrent, vcpuNumber).eq(VmwareInfo::getId, vmwareId));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 通过虚拟机ID查询宿主机信息
     *
     * @param vmwareId  虚拟机-ID
     * @return  HostInfo
     */
    private HostInfo queryHostInfoByVmwareId(Long vmwareId) {
        VmwareInfo vmwareInfo = getBaseMapper().selectOne(Wrappers.<VmwareInfo>lambdaQuery().select(VmwareInfo::getHostId).eq(VmwareInfo::getId, vmwareId));
        Assert.notNull(vmwareInfo, () -> new NotFoundException("未查询到虚拟机信息!"));
        return queryHostInfoByVmwareInfo(vmwareInfo);
    }

    /**
     * 通过虚拟机信息查询宿主机信息
     *
     * @param vmwareInfo    虚拟机信息
     * @return  HostInfo
     */
    private HostInfo queryHostInfoByVmwareInfo(VmwareInfo vmwareInfo) {
        Long hostId = vmwareInfo.getHostId();
        Assert.notNull(hostId, () -> new NotFoundException("未查询到虚拟机所属物理机ID信息!"));
        HostInfo hostInfo = hostInfoMapper.selectById(hostId);
        Assert.notNull(hostInfo, () -> new NotFoundException("未查询到虚拟机所属物理机信息!"));
        return hostInfo;
    }

    /**
     * 复杂参数处理示例
     */
    public List<VmwareErrorVO> complexVmware(List<Long> vmwareIds) {
        return commonVmwareOperation(
                vmwareIds,
                VmwareStateEnum.POWER_OFF,
                (vmwareInfo, hostInfo) -> new SubOperateParams(hostInfo.getIp(), vmwareInfo.getUuid(), "subParam"),
                (Function<SubOperateParams, R<String>>) subOperateParams -> agentVmwareClient.complexVmware(subOperateParams.getHostIp(), subOperateParams.getVmwareUuid(), subOperateParams.getSubParam())
        );
    }

    /**
     * 虚拟机通用操作封装
     *
     * @param vmwareIds 虚拟机 ID 列表
     * @param functionHandler  虚拟机操作函数
     * @return  List<VmwareErrorVO> 错误信息列表
     */
    private <T extends CommonOperateParams> List<VmwareErrorVO> commonVmwareOperation(List<Long> vmwareIds, VmwareStateEnum vmwareStateEnum, BiFunction<VmwareInfo, HostInfo, CommonOperateParams> functionParamsBuilder, Function<T, R<String>> functionHandler) {
        // 1.查询虚拟机相关信息
        List<VmwareInfo> vmwareInfoList = getBaseMapper().selectBatchIds(vmwareIds);
        Map<Long, HostInfo> hostInfoMap = queryHostInfoMap(vmwareInfoList);

        // 2.遍历虚拟机列表，执行相关操作
        List<VmwareErrorVO> errorInfoList = new ArrayList<>();
        for (VmwareInfo vmwareInfo : vmwareInfoList) {
            VmwareErrorVO vmwareErrorVO = singleVmwareOperate(
                    vmwareInfo.getId(),
                    functionParamsBuilder.apply(vmwareInfo, hostInfoMap.get(vmwareInfo.getHostId())),
                    functionHandler
            );

            if (vmwareErrorVO != null) {
                errorInfoList.add(vmwareErrorVO);
            } else {
                // 修改虚拟机状态
                getBaseMapper().update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, vmwareStateEnum).eq(VmwareInfo::getId, vmwareInfo.getId()));
            }
        }
        return errorInfoList;
    }

    /**
     * 虚拟机删除操作封装
     *
     * @param vmwareIds 虚拟机 ID 列表
     * @param functionHandler  虚拟机操作函数
     * @return  List<VmwareErrorVO> 错误信息列表
     */
    private <T extends CommonOperateParams> List<VmwareErrorVO> deleteVmwareOperation(List<Long> vmwareIds, BiFunction<VmwareInfo, HostInfo, CommonOperateParams> functionParamsBuilder, Function<T, R<String>> functionHandler) {
        // 1.查询虚拟机相关信息
        List<VmwareInfo> vmwareInfoList = getBaseMapper().selectBatchIds(vmwareIds);
        Map<Long, HostInfo> hostInfoMap = queryHostInfoMap(vmwareInfoList);

        // 2.遍历虚拟机列表，执行相关操作
        List<VmwareErrorVO> errorInfoList = new ArrayList<>();
        for (VmwareInfo vmwareInfo : vmwareInfoList) {
            VmwareErrorVO vmwareErrorVO = singleVmwareOperate(
                    vmwareInfo.getId(),
                    functionParamsBuilder.apply(vmwareInfo, hostInfoMap.get(vmwareInfo.getHostId())),
                    functionHandler
            );

            if (vmwareErrorVO != null) {
                errorInfoList.add(vmwareErrorVO);
            } else {
                // 删除虚拟机
                getBaseMapper().deleteById(vmwareInfo.getId());
            }
        }
        return errorInfoList;
    }


    /**
     * 根据虚拟机信息列表查询宿主机信息
     *
     * @param vmwareInfoList    虚拟机信息列表
     * @return  [{hostId, hostInfo}]
     */
    private Map<Long, HostInfo> queryHostInfoMap(List<VmwareInfo> vmwareInfoList) {
        // <vmwareId, hostId>
        Map<Long, Long> vmHostIdMap = vmwareInfoList.stream().collect(Collectors.toMap(VmwareInfo::getId, VmwareInfo::getHostId));
        List<Long> hostIds = new ArrayList<>(vmHostIdMap.values());
        // <hostId, hostInfo>
        return hostInfoMapper.selectBatchIds(hostIds).stream().collect(Collectors.toMap(HostInfo::getId, one -> one));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CommonOperateParams {
        public String hostIp;
        public String vmwareUuid;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    static class SubOperateParams extends CommonOperateParams{

        public SubOperateParams(String subParam) {
            this.subParam = subParam;
        }

        public SubOperateParams(String hostIp, String vmwareUuid) {
            super(hostIp, vmwareUuid);
        }

        public SubOperateParams(String hostIp, String vmwareUuid, String subParam) {
            super(hostIp, vmwareUuid);
            this.subParam = subParam;
        }

        public String subParam;
    }

    @SuppressWarnings("unchecked")
    private <T extends CommonOperateParams> VmwareErrorVO singleVmwareOperate(Long vmwareId, CommonOperateParams operateParams, Function<T, R<String>> functionHandler) {
        VmwareErrorVO vmwareErrorVO = null;
        RLock lock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareId);
        try {
            // 1.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(lock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException("虚拟机正在操作中，请稍后重试!"));

            // 2.Dubbo-操作
            R<String> vmwareOperationR = functionHandler.apply((T) operateParams);
            Assert.isTrue(vmwareOperationR.checkSuccess(), () -> new DubboFunctionException(vmwareOperationR.getMsg()));

            // 3.操作成功则打印启动日志
            log.info("{} Operation Message -> {}", vmwareId, vmwareOperationR.getData());

            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 启动失败则封装错误消息
            vmwareErrorVO = new VmwareErrorVO();
            vmwareErrorVO.setVmwareId(vmwareId);
            vmwareErrorVO.setErrorMessage(e.getMessage());
            return vmwareErrorVO;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
