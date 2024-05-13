package org.travis.center.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.checkerframework.checker.units.qual.Length;
import org.travis.center.common.enums.UserRoleEnum;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @ClassName UserRegisterDTO
 * @Description UserRegisterDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class UserRegisterDTO implements Serializable {
    /**
     * 登录用户名
     */
    @Schema(description="登录用户名")
    @NotBlank(message = "登录用户名不能为空!")
    private String username;

    /**
     * 登录密码
     */
    @Schema(description="登录密码")
    @NotBlank(message = "登录密码不能为空!")
    @Min(value = 6, message = "密码不能少于 6 位!")
    private String password;

    /**
     * 用户手机号
     */
    @Schema(description="用户手机号")
    @NotBlank(message = "用户手机号不能为空!")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请检查手机号码格式!")
    private String phone;

    /**
     * 用户邮箱地址
     */
    @Schema(description="用户邮箱地址")
    @NotBlank(message = "用户邮箱地址不能为空!")
    @Email(message = "邮件格式校验失败！")
    private String email;

    /**
     * 用户真实名字
     */
    @Schema(description="用户真实名字")
    @NotBlank(message = "用户真实名字不能为空!")
    private String realName;

    /**
     * 用户角色类型（1-管理员、2-普通用户）
     */
    @Schema(description="用户角色类型（1-管理员、2-普通用户）")
    @NotNull(message = "用户角色类型不能为空!")
    private UserRoleEnum roleType;

    /**
     * 用户个人介绍
     */
    @Schema(description="用户个人介绍")
    private String description;
}
