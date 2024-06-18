package org.travis.center.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName UserLoginDTO
 * @Description UserLoginDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class UserLoginDTO implements Serializable {
    /**
     * 登录用户名
     */
    @RequestLockKey
    @Schema(description="登录用户名")
    @NotBlank(message = "登录用户名不能为空!")
    private String username;

    /**
     * 登录密码
     */
    @Schema(description="登录密码")
    @NotBlank(message = "登录密码不能为空!")
    private String password;
}
