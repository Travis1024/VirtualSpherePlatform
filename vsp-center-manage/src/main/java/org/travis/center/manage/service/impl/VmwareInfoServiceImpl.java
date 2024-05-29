package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.api.pojo.bo.HostResourceInfoBO;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.creation.AbstractCreationService;
import org.travis.center.manage.creation.CreationHolder;
import org.travis.center.manage.creation.IsoCreationService;
import org.travis.center.manage.creation.SystemDiskCreationService;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;

/**
 * @ClassName VmwareInfoServiceImpl
 * @Description VmwareInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Service
public class VmwareInfoServiceImpl extends ServiceImpl<VmwareInfoMapper, VmwareInfo> implements VmwareInfoService{

    @DubboReference
    public AgentHostClient agentHostClient;
    @Resource
    private CreationHolder creationHolder;

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

    @Transactional
    @Override
    public VmwareInfo createVmwareInfo(VmwareInsertDTO vmwareInsertDTO) throws IOException {
        AbstractCreationService creationService = creationHolder.getCreationService(vmwareInsertDTO.getCreateForm().getValue());
        return creationService.build(vmwareInsertDTO);
    }
}
