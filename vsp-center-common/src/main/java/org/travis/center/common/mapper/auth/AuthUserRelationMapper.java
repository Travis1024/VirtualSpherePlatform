package org.travis.center.common.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.auth.AuthUserRelation;

/**
 * @ClassName AuthUserRelationMapper
 * @Description AuthUserRelationMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Mapper
public interface AuthUserRelationMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<AuthUserRelation> {
}
