package org.travis.center.support.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.support.pojo.dto.DynamicConfigUpdateDTO;
import org.travis.center.support.service.DynamicConfigInfoService;
import org.travis.center.support.utils.DynamicConfigUtil;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

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
        dynamicConfigUtil.updateConfigValue(dynamicConfigUpdateDTO.getId(), dynamicConfigUpdateDTO.getConfigValue().trim());
    }
}
