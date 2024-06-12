package org.travis.center.support.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigFixedEnum;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.support.pojo.dto.DynamicConfigUpdateDTO;
import org.travis.center.support.service.DynamicConfigInfoService;
import org.travis.center.support.utils.DynamicConfigUtil;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.exceptions.NotFoundException;

import javax.annotation.Resource;

/**
 * @ClassName DynamicConfigInfoServiceImpl
 * @Description DynamicConfigInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Service
public class DynamicConfigInfoServiceImpl extends ServiceImpl<DynamicConfigInfoMapper, DynamicConfigInfo> implements DynamicConfigInfoService{

    @Resource
    private DynamicConfigUtil dynamicConfigUtil;

    @Override
    public List<DynamicConfigInfo> selectList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<DynamicConfigInfo> pageSelectList(PageQuery pageQuery) {
        Page<DynamicConfigInfo> configInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(configInfoPage);
    }

    @Override
    public void updateConfigValue(DynamicConfigUpdateDTO dynamicConfigUpdateDTO) {
        // 1.判断当前动态配置是否允许修改
        Optional<DynamicConfigInfo> configInfoOptional = Optional.ofNullable(getById(dynamicConfigUpdateDTO.getId()));
        Assert.isTrue(configInfoOptional.isPresent(), () -> new NotFoundException("未查询到当前配置!"));
        DynamicConfigInfo configInfo = configInfoOptional.get();
        if (configInfo.getIsFixed().getValue().equals(DynamicConfigFixedEnum.DISALLOW_UPDATE.getValue())) {
            throw new ForbiddenException("当前动态配置禁止修改!");
        }
        // 2.修改动态配置 value
        dynamicConfigUtil.updateConfigValue(dynamicConfigUpdateDTO.getId(), dynamicConfigUpdateDTO.getConfigValue().trim());
    }
}
