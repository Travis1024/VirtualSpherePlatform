package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentDiskClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.DiskInfoMapper;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.dto.DiskAttachDTO;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.travis.shared.common.constants.DiskConstant;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.*;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DiskInfoServiceImpl
 * @Description DiskInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
@Service
public class DiskInfoServiceImpl extends ServiceImpl<DiskInfoMapper, DiskInfo> implements DiskInfoService{

    @DubboReference
    public AgentDiskClient agentDiskClient;
    @Resource
    public AgentAssistService agentAssistService;
    @Resource
    public HostInfoMapper hostInfoMapper;
    @Resource
    public RedissonClient redissonClient;
    @Resource
    public VmwareInfoMapper vmwareInfoMapper;

    @Override
    public DiskInfo selectOne(Long diskId) {
        return getById(diskId);
    }

    @Override
    public List<DiskInfo> selectList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<DiskInfo> pageSelectList(PageQuery pageQuery) {
        Page<DiskInfo> diskInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(diskInfoPage);
    }

    @Transactional
    @Override
    public DiskInfo createDisk(DiskInsertDTO diskInsertDTO) {
        /*
          1.组装并保存磁盘信息
         */
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setId(SnowflakeIdUtil.nextId());
        diskInfo.setName(diskInsertDTO.getName());
        diskInfo.setDescription(diskInsertDTO.getDescription());
        diskInfo.setSpaceSize(diskInsertDTO.getSpaceSize());

        // 设置磁盘子路径（包含文件名）
        String subPath = DiskConstant.SUB_DISK_PATH_PREFIX + File.separator + DiskConstant.DISK_NAME_DATA_PREFIX + diskInfo.getId() + DiskConstant.DISK_NAME_SUFFIX;
        diskInfo.setSubPath(subPath);

        diskInfo.setDiskType(DiskTypeEnum.DATA);
        diskInfo.setIsMount(DiskMountEnum.UN_MOUNTED);

        /*
          2.发送创建磁盘请求
         */
        createDiskRequest(diskInfo);
        return diskInfo;
    }

    @Override
    public void createDiskRequest(DiskInfo diskInfo) {
        // 1.校验磁盘大小（必须以 GB 为整数单位）
        Assert.isTrue(diskInfo.getSpaceSize() % SystemConstant.GB_UNIT == 0, () -> new BadRequestException("磁盘大小必须为整-GB!"));
        // 2.持久化到数据库
        VspStrUtil.trimStr(diskInfo);
        save(diskInfo);
        // 3.组装请求参数
        List<String> agentIpList = agentAssistService.getHealthyHostAgentIpList();
        String serverAgentIp = agentIpList.get(RandomUtil.randomInt(0, agentIpList.size()));
        String sharedStoragePath = agentAssistService.getHostSharedStoragePath();
        // 4.Dubbo 创建磁盘
        R<String> createDiskR = agentDiskClient.createDisk(serverAgentIp, sharedStoragePath + diskInfo.getSubPath(), diskInfo.getSpaceSize() / SystemConstant.GB_UNIT);
        Assert.isTrue(createDiskR.checkSuccess(), () -> new DubboFunctionException(createDiskR.getMsg()));
        log.info("磁盘创建成功! -> {}", sharedStoragePath + diskInfo.getSubPath());
    }

    @Transactional
    @Override
    public void deleteDisk(Long diskId, boolean isUserDelete) {
        // 1.删除前校验
        Optional<DiskInfo> diskInfoOptional = Optional.ofNullable(getById(diskId));
        DiskInfo diskInfo = diskInfoOptional.orElseThrow(() -> new BadRequestException("未找到磁盘信息!"));
        Assert.isFalse(isUserDelete && DiskTypeEnum.ROOT.getValue().equals(diskInfo.getDiskType().getValue()), () -> new BadRequestException("用户无权删除系统磁盘!"));
        Assert.isFalse(diskInfo.getIsMount().getValue().equals(DiskMountEnum.MOUNTED.getValue()), () -> new BadRequestException("磁盘处于挂载状态, 请先卸载磁盘!"));

        // 2.删除数据库信息
        removeById(diskId);

        // 3.删除磁盘
        List<String> agentIpList = agentAssistService.getHealthyHostAgentIpList();
        String serverAgentIp = agentIpList.get(RandomUtil.randomInt(0, agentIpList.size()));
        String sharedStoragePath = agentAssistService.getHostSharedStoragePath();
        // Dubbo 删除磁盘
        R<String> deleteDiskR = agentDiskClient.deleteDisk(serverAgentIp, sharedStoragePath + diskInfo.getSubPath());
        Assert.isTrue(deleteDiskR.checkSuccess(), () -> new DubboFunctionException(deleteDiskR.getMsg()));
    }

    @Override
    public List<DiskInfo> selectListByVmwareId(Long vmwareId) {
        return getBaseMapper().selectList(Wrappers.<DiskInfo>lambdaQuery().eq(DiskInfo::getVmwareId, vmwareId));
    }

    @Override
    public PageResult<DiskInfo> pageSelectListByVmwareId(PageQuery pageQuery, Long vmwareId) {
        Page<DiskInfo> diskInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), Wrappers.<DiskInfo>lambdaQuery().eq(DiskInfo::getVmwareId, vmwareId));
        return PageResult.of(diskInfoPage);
    }

    @Transactional
    @Override
    public void attachDisk(DiskAttachDTO diskAttachDTO) {
        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + diskAttachDTO.getVmwareId());
        try {
            // 1.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException(BizCodeEnum.LOCKED.getCode(), "虚拟机正在操作中，请稍后重试!"));

            // 2.校验当前虚拟机当前盘符是否存在
            Optional<DiskInfo> diskInfoConflictOptional = Optional.ofNullable(getBaseMapper().selectOne(
                    Wrappers.<DiskInfo>lambdaQuery()
                            .eq(DiskInfo::getVmwareId, diskAttachDTO.getVmwareId())
                            .eq(DiskInfo::getTargetDev, diskAttachDTO.getTargetDev())
            ));
            Assert.isTrue(diskInfoConflictOptional.isPresent(), () -> new BadRequestException("当前虚拟机已存在该盘符!"));

            // 3.查询磁盘信息
            Optional<DiskInfo> diskInfoOptional = Optional.ofNullable(getById(diskAttachDTO.getDiskId()));
            DiskInfo diskInfo = diskInfoOptional.orElseThrow(() -> new NotFoundException("未找到磁盘信息!"));
            Assert.isTrue(diskInfo.getIsMount().getValue().equals(DiskMountEnum.UN_MOUNTED.getValue()), () -> new BadRequestException("磁盘已挂载!"));

            // 4.查询虚拟机信息
            Optional<VmwareInfo> vmwareInfoOptional = Optional.ofNullable(vmwareInfoMapper.selectById(diskAttachDTO.getVmwareId()));
            VmwareInfo vmwareInfo = vmwareInfoOptional.orElseThrow(() -> new NotFoundException("未找到虚拟机信息!"));

            // 5.查询虚拟机所属宿主机IP
            Long hostId = vmwareInfo.getHostId();
            Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoMapper.selectById(hostId));
            HostInfo hostInfo = hostInfoOptional.orElseThrow(() -> new NotFoundException("未找到宿主机信息!"));

            // 6.Dubbo-执行磁盘挂载命令
            String hostSharedStoragePath = agentAssistService.getHostSharedStoragePath();
            R<String> attachDiskR = agentDiskClient.attachDisk(hostInfo.getIp(), vmwareInfo.getUuid(), hostSharedStoragePath + diskInfo.getSubPath(), diskAttachDTO.getTargetDev());
            Assert.isTrue(attachDiskR.checkSuccess(), () -> new DubboFunctionException(attachDiskR.getMsg()));

            // 7.修改磁盘挂载状态
            getBaseMapper().update(
                    Wrappers.<DiskInfo>lambdaUpdate()
                            .set(DiskInfo::getVmwareId, diskAttachDTO.getVmwareId())
                            .set(DiskInfo::getIsMount, DiskMountEnum.MOUNTED)
                            .set(DiskInfo::getTargetDev, diskAttachDTO.getTargetDev())
                            .eq(DiskInfo::getId, diskAttachDTO.getDiskId())
            );

        } catch (CommonException commonException) {
            log.error(commonException.getMessage(), commonException);
            throw commonException;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    @Override
    public void detachDisk(Long diskId) {
        // 1.查询磁盘信息
        Optional<DiskInfo> diskInfoOptional = Optional.ofNullable(getById(diskId));
        DiskInfo diskInfo = diskInfoOptional.orElseThrow(() -> new NotFoundException("未找到磁盘信息!"));
        Assert.isTrue(DiskMountEnum.MOUNTED.equals(diskInfo.getIsMount()), () -> new BadRequestException("磁盘未挂载!"));

        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + diskInfo.getVmwareId());
        try {
            // 2.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException(BizCodeEnum.LOCKED.getCode(), "虚拟机正在操作中，请稍后重试!"));

            // 3.查询虚拟机信息
            Optional<VmwareInfo> vmwareInfoOptional = Optional.ofNullable(vmwareInfoMapper.selectById(diskInfo.getVmwareId()));
            VmwareInfo vmwareInfo = vmwareInfoOptional.orElseThrow(() -> new NotFoundException("未找到虚拟机信息!"));

            // 4.查询虚拟机所属宿主机IP
            Long hostId = vmwareInfo.getHostId();
            Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoMapper.selectById(hostId));
            HostInfo hostInfo = hostInfoOptional.orElseThrow(() -> new NotFoundException("未找到宿主机信息!"));

            // 5.校验虚拟机是否是关机状态
            Assert.isTrue(VmwareStateEnum.SHUT_OFF.equals(vmwareInfo.getState()), () -> new BadRequestException("虚拟机处于非关机状态, 无法进行磁盘卸载!"));

            // 6.Dubbo-卸载磁盘
            R<String> detachDiskR = agentDiskClient.detachDisk(hostInfo.getIp(), vmwareInfo.getUuid(), diskInfo.getTargetDev());
            Assert.isTrue(detachDiskR.checkSuccess(), () -> new DubboFunctionException(detachDiskR.getMsg()));

            // 7.修改磁盘状态
            getBaseMapper().update(
                    Wrappers.<DiskInfo>lambdaUpdate()
                            .set(DiskInfo::getIsMount, DiskMountEnum.UN_MOUNTED)
                            .set(DiskInfo::getTargetDev, null)
                            .set(DiskInfo::getVmwareId, null)
                            .eq(DiskInfo::getId, diskId)
            );

        } catch (CommonException commonException) {
            log.error(commonException.getMessage(), commonException);
            throw commonException;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
