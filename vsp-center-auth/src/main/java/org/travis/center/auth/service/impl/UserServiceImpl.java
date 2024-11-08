package org.travis.center.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.travis.center.auth.pojo.dto.UserModifyPasswordDTO;
import org.travis.center.auth.pojo.dto.UserModifyRoleDTO;
import org.travis.center.auth.pojo.dto.UserRegisterDTO;
import org.travis.center.auth.pojo.dto.UserUpdateDTO;
import org.travis.center.common.mapper.auth.UserMapper;
import org.travis.center.common.entity.auth.User;
import org.travis.center.auth.service.UserService;
import org.travis.center.common.service.UserAssistService;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.UserThreadLocalUtil;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;

/**
 * @ClassName UserServiceImpl
 * @Description UserServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    public UserAssistService userAssistService;

    @Override
    public void login(String username, String password) {
        Optional<User> userOptional = Optional.ofNullable(getOne(Wrappers.<User>lambdaQuery().select(User::getId, User::getPassword).eq(User::getUsername, username)));
        Assert.isFalse(userOptional.isEmpty(), () -> new BadRequestException("用户名或密码错误!"));
        Assert.isTrue(BCrypt.checkpw(password, userOptional.get().getPassword()), () -> new BadRequestException("用户名或密码错误!"));
        StpUtil.login(userOptional.get().getId());
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, ForbiddenException::new);

        // 2.校验用户名是否存在
        Optional<User> userOptional = Optional.ofNullable(getOne(Wrappers.<User>lambdaQuery().select(User::getId).eq(User::getUsername, userRegisterDTO.getUsername())));
        userOptional.ifPresent(data -> {
            throw new BadRequestException("当前用户名已存在!");
        });

        // 3.数据库记录存储
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        user.setId(SnowflakeIdUtil.nextId());
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
        VspStrUtil.trimStr(user);
        save(user);
    }

    @Override
    public User queryById(Long userId) {
        Optional<User> userOptional = Optional.ofNullable(getById(userId));
        userOptional.orElseThrow(() -> new BadRequestException("未查询到用户信息!"));
        return userOptional.get().setPassword(null);
    }

    // @Override
    // public boolean checkAdminUser(Long userId) {
    //     Optional<User> userOptional = Optional.ofNullable(getOne(Wrappers.<User>lambdaQuery().select(User::getRoleType).eq(User::getId, userId)));
    //     userOptional.orElseThrow(() -> new BadRequestException("未查询到用户信息!"));
    //     return userOptional.get().getRoleType().getValue().equals(UserRoleEnum.ADMIN_USER.getValue());
    // }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) {
        // 1.判断更新用户是否为当前登录用户, 判断是否为管理员用户
        if (!userUpdateDTO.getId().equals(UserThreadLocalUtil.getUserId()) && !userAssistService.checkAdminUser()) {
            throw new ForbiddenException();
        }
        // 2.更新用户信息
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        updateById(user);
    }

    @Override
    public void updatePassword(UserModifyPasswordDTO userModifyPasswordDTO) {
        // 1.判断更新用户是否为当前登录用户, 判断是否为管理员用户
        if (!userModifyPasswordDTO.getId().equals(UserThreadLocalUtil.getUserId()) && !userAssistService.checkAdminUser()) {
            throw new ForbiddenException();
        }

        // 2.获取数据库中用户密码
        String databasePassword = getById(userModifyPasswordDTO.getId()).getPassword();

        // 3.检测原密码是否正确
        Assert.isTrue(BCrypt.checkpw(userModifyPasswordDTO.getOldPassword(), databasePassword), () -> new BadRequestException("用户原密码错误!"));

        // 4.更新数据库密码
        update(Wrappers.<User>lambdaUpdate().set(User::getPassword, BCrypt.hashpw(userModifyPasswordDTO.getNewPassword(), BCrypt.gensalt(12))).eq(User::getId, userModifyPasswordDTO.getId()));
    }

    @Override
    public void updateUserRole(UserModifyRoleDTO userModifyRoleDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, ForbiddenException::new);
        // 2.修改用户权限信息
        update(Wrappers.<User>lambdaUpdate().set(User::getRoleType, userModifyRoleDTO.getRoleType()).eq(User::getId, userModifyRoleDTO.getId()));
    }

    @Override
    public void userDelete(Long userId) {
        // 1.判断更新用户是否为当前登录用户, 判断是否为管理员用户
        if (!userId.equals(UserThreadLocalUtil.getUserId()) && !userAssistService.checkAdminUser()) {
            throw new ForbiddenException();
        }
        // 2.执行删除操作
        removeById(userId);
        // 3.退出登录
        StpUtil.logout(userId);
    }
}
