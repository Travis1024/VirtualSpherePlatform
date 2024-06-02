package org.travis.center.support.service;

import org.travis.center.common.entity.support.DynamicConfigInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.support.pojo.dto.DynamicConfigUpdateDTO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.util.List;

/**
 * @ClassName DynamicConfigInfoService
 * @Description DynamicConfigInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
public interface DynamicConfigInfoService extends IService<DynamicConfigInfo>{

    List<DynamicConfigInfo> selectList();

    PageResult<DynamicConfigInfo> pageSelectList(PageQuery pageQuery);

    void updateConfigValue(DynamicConfigUpdateDTO dynamicConfigUpdateDTO);
}
