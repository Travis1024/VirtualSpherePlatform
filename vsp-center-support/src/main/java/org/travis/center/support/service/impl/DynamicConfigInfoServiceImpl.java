package org.travis.center.support.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.support.pojo.dto.DynamicConfigUpdateDTO;
import org.travis.center.support.processor.AbstractDynamicConfigHolder;
import org.travis.center.support.service.DynamicConfigInfoService;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.enums.MonitorPeriodEnum;
import org.travis.shared.common.exceptions.NotFoundException;

/**
 * @ClassName DynamicConfigInfoServiceImpl
 * @Description DynamicConfigInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Service
public class DynamicConfigInfoServiceImpl extends ServiceImpl<DynamicConfigInfoMapper, DynamicConfigInfo> implements DynamicConfigInfoService{


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
        // 1.查询动态配置信息
        DynamicConfigInfo dynamicConfigInfo = Optional.ofNullable(getById(dynamicConfigUpdateDTO.getId())).orElseThrow(() -> new NotFoundException("未查询到当前配置!"));
        // 2.修改动态配置值
        AbstractDynamicConfigHolder.getDynamicConfigHandler(dynamicConfigInfo.getConfigType()).executeUpdateValue(dynamicConfigInfo, dynamicConfigUpdateDTO.getConfigValue());
    }

    @Override
    public List<MonitorPeriodEnum> queryMonitorPeriodSelectableList() {
        return List.of(MonitorPeriodEnum.values());
    }
}
