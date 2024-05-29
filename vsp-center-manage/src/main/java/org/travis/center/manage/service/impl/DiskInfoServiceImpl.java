package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentDiskClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.mapper.manage.DiskInfoMapper;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.travis.shared.common.constants.DiskConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Optional;

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
    private AgentAssistService agentAssistService;
    @Resource
    private HostInfoMapper hostInfoMapper;

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
    public DiskInfo createDisk(DiskInsertDTO diskInsertDTO, boolean isUserCreate) {
        /*
          1.组装并保存磁盘信息
         */
        Assert.isFalse(isUserCreate && DiskTypeEnum.ROOT.getValue().equals(diskInsertDTO.getDiskType().getValue()), () -> new BadRequestException("用户无权创建系统磁盘!"));
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setId(SnowflakeIdUtil.nextId());
        // 设置磁盘默认为“未挂载”
        diskInfo.setIsMount(DiskMountEnum.UN_MOUNTED);
        BeanUtils.copyProperties(diskInsertDTO, diskInfo);
        // 组织磁盘名称及子路径
        String diskName = (diskInfo.getDiskType().getValue().equals(DiskTypeEnum.ROOT.getValue()) ? DiskConstant.DISK_NAME_ROOT_PREFIX : DiskConstant.DISK_NAME_DATA_PREFIX) + diskInfo.getId() + DiskConstant.DISK_NAME_SUFFIX;
        String subPath = DiskConstant.SUB_DISK_PATH_PREFIX + File.separator + diskName;
        // 设置磁盘名称
        diskInfo.setName(diskName);
        // 设置磁盘子路径（包含文件名）
        diskInfo.setSubPath(subPath);
        // 校验磁盘大小（必须以 GB 为整数单位）
        Assert.isTrue(diskInfo.getSpaceSize() % SystemConstant.GB_UNIT == 0, () -> new BadRequestException("磁盘大小必须为整-GB!"));
        VspStrUtil.trimStr(diskInfo);
        save(diskInfo);

        /*
          2.发送磁盘创建请求
         */
        // 组装请求参数
        List<String> agentIpList = agentAssistService.getHealthyHostAgentIpList();
        String serverAgentIp = agentIpList.get(RandomUtil.randomInt(0, agentIpList.size()));
        String sharedStoragePath = agentAssistService.getHostSharedStoragePath();
        // Dubbo 创建磁盘
        R<String> createDiskR = agentDiskClient.createDisk(serverAgentIp, sharedStoragePath + subPath, diskInfo.getId() / SystemConstant.GB_UNIT);
        Assert.isTrue(createDiskR.checkSuccess(), () -> new DubboFunctionException(createDiskR.getMsg()));
        log.info("磁盘创建成功! -> {}", sharedStoragePath + subPath);

        return diskInfo;
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
}
