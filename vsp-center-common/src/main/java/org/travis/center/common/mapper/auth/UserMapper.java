package org.travis.center.common.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.auth.User;

/**
 * @ClassName UserMapper
 * @Description UserMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Mapper
public interface UserMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<User> {
}
