package org.travis.center.auth.controller;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.travis.center.auth.pojo.dto.UserLoginDTO;
import org.travis.center.auth.pojo.dto.UserRegisterDTO;
import org.travis.center.auth.service.UserService;
import org.travis.center.common.entity.auth.User;
import org.travis.center.auth.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;

    @Operation(summary = "查询当前登录用户信息")
    @GetMapping("/queryCur")
    public User queryCurUserInfo() {
        return userService.queryById(UserThreadLocalUtil.getUserId());
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public void login(@Validated @RequestBody UserLoginDTO userLoginDTO) {
        userService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());
    }

    @Operation(summary = "新用户注册")
    @PostMapping("/register")
    private void register(@Validated @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
    }
}
