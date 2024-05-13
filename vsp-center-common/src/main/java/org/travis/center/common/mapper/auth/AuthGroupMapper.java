package org.travis.center.common.mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.auth.AuthGroup;

/**
 * @ClassName AuthGroupMapper
 * @Description AuthGroupMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Mapper
public interface AuthGroupMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<AuthGroup> {
}
