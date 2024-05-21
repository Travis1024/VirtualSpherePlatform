package org.travis.center.common.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.travis.center.common.entity.auth.User;
import org.travis.center.common.enums.UserRoleEnum;
import org.travis.center.common.mapper.auth.UserMapper;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.UserThreadLocalUtil;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @ClassName UserAssistService
 * @Description UserAssistService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/21
 */
@Slf4j
@Service
public class UserAssistService {
    @Resource
    private UserMapper userMapper;

    /**
     * @MethodName checkAdminUser
     * @Description 校验用户是否为管理员用户
     * @Author travis-wei
     * @Data 2024/5/21
     * @param userId    用户ID
     * @Return boolean  true-管理员，false-非管理员
     **/
    public boolean checkAdminUser(Long userId) {
        Optional<User> userOptional = Optional.ofNullable(userMapper.selectOne(Wrappers.<User>lambdaQuery().select(User::getRoleType).eq(User::getId, userId)));
        userOptional.orElseThrow(() -> new BadRequestException("未查询到用户信息!"));
        return userOptional.get().getRoleType().getValue().equals(UserRoleEnum.ADMIN_USER.getValue());
    }

    /**
     * @MethodName checkAdminUser
     * @Description 校验当前登录用户是否为管理员用户
     * @Author travis-wei
     * @Data 2024/5/21
     * @Return boolean  true-管理员，false-非管理员
     **/
    public boolean checkAdminUser() {
        Long userId = UserThreadLocalUtil.getUserId();
        Assert.isFalse(userId == null, () -> new BadRequestException("未读取到当前登录用户!"));
        return checkAdminUser(userId);
    }
}
