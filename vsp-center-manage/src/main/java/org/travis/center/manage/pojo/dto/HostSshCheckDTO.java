package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName HostSshCheckDTO
 * @Description HostSshCheckDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class HostSshCheckDTO implements Serializable {

    @Schema(description = "宿主机 IP 地址")
    @NotBlank(message = "宿主机 IP 地址不能为空!")
    private String hostIp;

    @Schema(description = "宿主机 SSH 连接端口")
    @NotNull(message = "宿主机 SSH 连接端口不能为空!")
    private Integer hostSshPort;

    @Schema(description = "宿主机管理员用户名")
    @NotBlank(message = "宿主机管理员用户名不能为空!")
    private String username;

    @Schema(description = "宿主机管理员密码")
    @NotBlank(message = "宿主机管理员密码不能为空!")
    private String password;
}
