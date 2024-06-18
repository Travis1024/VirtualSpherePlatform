package org.travis.center.manage.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName HostUpdateDTO
 * @Description HostUpdateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Data
public class HostUpdateDTO implements Serializable {
    /**
     * ID
     */
    @RequestLockKey
    @Schema(description="ID")
    @NotNull(message = "宿主机 ID 不能为空!")
    private Long id;

    /**
     * 宿主机名称
     */
    @Schema(description="宿主机名称")
    private String name;

    /**
     * 宿主机描述信息
     */
    @Schema(description="宿主机描述信息")
    private String description;

    /**
     * 宿主机管理员登录用户
     */
    @Schema(description="宿主机管理员登录用户")
    private String loginUser;

    /**
     * 宿主机管理员登录密码
     */
    @Schema(description="宿主机管理员登录密码")
    private String loginPassword;

    /**
     * 宿主机 SSH 连接端口号
     */
    @Schema(description="宿主机 SSH 连接端口号")
    private Integer sshPort;
}
