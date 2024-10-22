package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName VmwareSshCheckDTO
 * @Description VmwareSshCheckDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class VmwareSshCheckDTO implements Serializable {

    @Schema(description = "虚拟机ID")
    @NotNull(message = "虚拟机ID不能为空!")
    private Long vmwareId;

    @Schema(description = "虚拟机管理员登录用户名")
    @NotBlank(message = "宿主机管理员用户名不能为空!")
    private String username;

    @Schema(description = "虚拟机管理员登录密码")
    @NotBlank(message = "虚拟机管理员密码不能为空!")
    private String password;
}
