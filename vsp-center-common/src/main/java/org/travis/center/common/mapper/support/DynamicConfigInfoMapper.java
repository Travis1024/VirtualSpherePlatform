package org.travis.center.common.mapper.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.support.DynamicConfigInfo;

/**
 * @ClassName DynamicConfigInfoMapper
 * @Description DynamicConfigInfoMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Mapper
public interface DynamicConfigInfoMapper extends BaseMapper<DynamicConfigInfo> {
}
