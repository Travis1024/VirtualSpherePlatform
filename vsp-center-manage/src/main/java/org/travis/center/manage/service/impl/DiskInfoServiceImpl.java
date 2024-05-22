package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentDiskClient;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.mapper.manage.DiskInfoMapper;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.travis.shared.common.constants.DiskConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @ClassName DiskInfoServiceImpl
 * @Description DiskInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class DiskInfoServiceImpl extends ServiceImpl<DiskInfoMapper, DiskInfo> implements DiskInfoService{

    public static final Long GB_UNIT = 1024L * 1024L * 1024L;
    @DubboReference
    private AgentDiskClient agentDiskClient;
    @Resource
    private AgentAssistService agentAssistService;

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
        BeanUtils.copyProperties(diskInsertDTO, diskInfo);
        // 组织磁盘名称
        String diskName = (diskInfo.getDiskType().getValue().equals(DiskTypeEnum.ROOT.getValue()) ? DiskConstant.DISK_NAME_ROOT_PREFIX : DiskConstant.DISK_NAME_DATA_PREFIX) + diskInfo.getId();
        // 设置磁盘名称
        diskInfo.setName(diskName);
        // 设置磁盘子路径（包含文件名）
        diskInfo.setSubPath(DiskConstant.SUB_DISK_PATH_PREFIX + File.separator + diskName);
        // 校验磁盘大小（必须以 GB 为整数单位）
        Assert.isTrue(diskInfo.getSpaceSize() % GB_UNIT == 0, () -> new BadRequestException("磁盘大小必须为整-GB!"));
        VspStrUtil.trimStr(diskInfo);
        save(diskInfo);

        /*
          2.发送磁盘创建请求
         */
        List<String> agentIpList = agentAssistService.getHealthyHostAgentIpList();
        String serverAgentIp = agentIpList.get(RandomUtil.randomInt(0, agentIpList.size()));
        // TODO 继续完善
        agentDiskClient.createDisk(serverAgentIp);

        return diskInfo;
    }
}
