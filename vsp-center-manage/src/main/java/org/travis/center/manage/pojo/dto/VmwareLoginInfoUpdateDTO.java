package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName VmwareLoginInfoUpdateDTO
 * @Description VmwareLoginInfoUpdateDTO
 * @Author Travis
 * @Data 2024/10
 */
@Data
public class VmwareLoginInfoUpdateDTO implements Serializable {

    @RequestLockKey
    @Schema(description = "虚拟机ID")
    @NotNull(message = "虚拟机ID不能为空！")
    private Long vmwareId;

    @Schema(description = "虚拟机登录用户名")
    @NotBlank(message = "虚拟机登录用户名不能为空！")
    private String username;

    @Schema(description = "虚拟机登录密码")
    @NotBlank(message = "虚拟机登录密码不能为空！")
    private String password;

}