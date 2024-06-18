package org.travis.center.auth.controller;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.travis.center.auth.pojo.dto.*;
import org.travis.center.auth.service.UserService;
import org.travis.center.common.entity.auth.User;
import org.springframework.web.bind.annotation.*;

import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.aspect.RequestLock;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.UserThreadLocalUtil;

import javax.annotation.Resource;

/**
* 用户信息表(VSP.VSP_USER)表控制层
*
* @author xxxxx
*/
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    public UserService userService;

    @Log(title = "查询当前登录用户信息", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询当前登录用户信息")
    @GetMapping("/queryCur")
    public User queryCurUserInfo() {
        return userService.queryById(UserThreadLocalUtil.getUserId());
    }

    @RequestLock
    @Log(title = "用户登录")
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public void login(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        userService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());
    }

    @Log(title = "用户退出登录")
    @Operation(summary = "用户退出登录")
    @GetMapping("/logout")
    public void logout() {
        StpUtil.logout();
    }

    @RequestLock
    @Log(title = "用户信息删除", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "用户信息删除")
    @DeleteMapping("/delete")
    public void userDelete(@RequestLockKey @RequestParam("userId") Long userId) {
        Assert.notNull(userId, () -> new BadRequestException("用户-ID不能为空!"));
        userService.userDelete(userId);
    }

    @RequestLock
    @Log(title = "新用户注册", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "新用户注册")
    @PostMapping("/register")
    public void register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
    }

    @RequestLock
    @Log(title = "用户基础信息更新", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "用户基础信息更新")
    @PutMapping("/update")
    public void updateUserInfo(@Validated @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserInfo(userUpdateDTO);
    }

    @RequestLock
    @Log(title = "用户密码修改", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "用户密码修改")
    @PutMapping("/modifyPw")
    public void updatePassword(@Validated @RequestBody UserModifyPasswordDTO userModifyPasswordDTO) {
        userService.updatePassword(userModifyPasswordDTO);
    }

    @RequestLock
    @Log(title = "用户角色修改", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "用户角色修改")
    @PutMapping("/modifyRole")
    public void updateUserRole(@Validated @RequestBody UserModifyRoleDTO userModifyRoleDTO) {
        userService.updateUserRole(userModifyRoleDTO);
    }
}
