package org.travis.center.auth.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UserModifyPasswordDTO
 * @Description UserModifyPasswordDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class UserModifyPasswordDTO implements Serializable {
    /**
     * ID
     */
    @RequestLockKey
    @Schema(description="ID")
    @NotNull(message = "用户ID不能为空!")
    private Long id;

    /**
     * 原登录密码
     */
    @Schema(description="原登录密码")
    @NotBlank(message = "原登录密码不能为空!")
    private String oldPassword;

    /**
     * 新登录密码
     */
    @Schema(description="新登录密码")
    @NotBlank(message = "新登录密码不能为空!")
    @Min(value = 6, message = "密码长度不能小于 6 位!")
    private String newPassword;
}
