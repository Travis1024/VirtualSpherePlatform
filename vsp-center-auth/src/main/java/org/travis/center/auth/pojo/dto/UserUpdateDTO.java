package org.travis.center.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UserUpdateDTO
 * @Description UserUpdateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class UserUpdateDTO implements Serializable {
    /**
     * ID
     */
    @NotNull(message = "用户ID不能为空!")
    @Schema(description="ID")
    private Long id;

    /**
     * 用户手机号
     */
    @Schema(description="用户手机号")
    private String phone;

    /**
     * 用户邮箱地址
     */
    @Schema(description="用户邮箱地址")
    private String email;

    /**
     * 用户真实名字
     */
    @Schema(description="用户真实名字")
    private String realName;

    /**
     * 用户个人介绍
     */
    @Schema(description="用户个人介绍")
    private String description;
}
