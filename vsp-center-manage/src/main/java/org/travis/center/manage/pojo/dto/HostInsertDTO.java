package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName HostInsertDTO
 * @Description HostInsertDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class HostInsertDTO implements Serializable {
    /**
     * 宿主机名称
     */
    @Schema(description="宿主机名称")
    @NotBlank(message = "宿主机名称不能为空!")
    private String name;

    /**
     * 宿主机描述信息
     */
    @Schema(description="宿主机描述信息")
    private String description;

    /**
     * 宿主机 IP 地址
     */
    @Schema(description="宿主机 IP 地址")
    @NotBlank(message = "宿主机 IP 地址不能为空!")
    private String ip;

    /**
     * 宿主机管理员登录用户
     */
    @Schema(description="宿主机管理员登录用户")
    @NotBlank(message = "宿主机管理员登录用户不能为空!")
    private String loginUser;

    /**
     * 宿主机管理员登录密码
     */
    @Schema(description="宿主机管理员登录密码")
    @NotBlank(message = "宿主机管理员登录密码不能为空!")
    private String loginPassword;

    /**
     * 宿主机 SSH 连接端口号
     */
    @Schema(description="宿主机 SSH 连接端口号")
    @NotNull(message = "宿主机 SSH 连接端口号不能为空!")
    private Integer sshPort;

    /**
     * 宿主机所属二层网络 ID
     */
    @Schema(description="宿主机所属二层网络 ID")
    @NotNull(message = "宿主机所属二层网络 ID 不能为空!")
    private Long networkLayerId;

    /**
     * 宿主机共享存储路径
     */
    @Schema(description="宿主机共享存储路径")
    @NotBlank(message = "宿主机共享存储路径不能为空!")
    private String sharedStoragePath;
}
