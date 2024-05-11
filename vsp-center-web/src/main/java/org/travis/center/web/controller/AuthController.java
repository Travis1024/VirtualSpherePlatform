package org.travis.center.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.travis.shared.common.exceptions.BadRequestException;

/**
 * @ClassName AuthController
 * @Description AuthController
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
@Slf4j
@RestController
@RequestMapping("/web/auth")
public class AuthController {
    @Operation(summary = "用户登录")
    @GetMapping("/login")
    public void login(@RequestParam("username") String username, @RequestParam("password") String password) {
        if (!"admin".equals(username) || !"123456".equals(password)) {
            log.error("登录失败!");
            throw new BadRequestException("用户名或密码错误!");
        }
        StpUtil.login(1289128712L);
    }
}
