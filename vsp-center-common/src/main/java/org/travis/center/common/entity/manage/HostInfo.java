package org.travis.center.common.entity.manage;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.travis.center.common.enums.HostStateEnum;

/**
 * @ClassName HostInfo
 * @Description HostInfo
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */

@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_HOST_INFO")
public class HostInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<HostInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 宿主机名称
     */
    @TableField(value = "\"NAME\"")
    @Schema(description="宿主机名称")
    private String name;

    /**
     * 宿主机描述信息
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="宿主机描述信息")
    private String description;

    /**
     * 宿主机 IP 地址
     */
    @TableField(value = "IP")
    @Schema(description="宿主机 IP 地址")
    private String ip;

    /**
     * 宿主机内存大小（字节）
     */
    @TableField(value = "MEMORY_SIZE")
    @Schema(description="宿主机内存大小（字节）")
    private Long memorySize;

    /**
     * 宿主机 CPU 核数
     */
    @TableField(value = "CPU_NUMBER")
    @Schema(description="宿主机 CPU 核数")
    private Integer cpuNumber;

    /**
     * 宿主机架构信息
     */
    @TableField(value = "ARCHITECTURE")
    @Schema(description="宿主机架构信息")
    private String architecture;

    /**
     * 宿主机管理员登录用户
     */
    @TableField(value = "LOGIN_USER")
    @Schema(description="宿主机管理员登录用户")
    private String loginUser;

    /**
     * 宿主机管理员登录密码
     */
    @TableField(value = "LOGIN_PASSWORD")
    @Schema(description="宿主机管理员登录密码")
    private String loginPassword;

    /**
     * 宿主机 SSH 连接端口号
     */
    @TableField(value = "SSH_PORT")
    @Schema(description="宿主机 SSH 连接端口号")
    private Integer sshPort;

    /**
     * 宿主机所属二层网络 ID
     */
    @TableField(value = "NETWORK_LAYER_ID")
    @Schema(description="宿主机所属二层网络 ID")
    private Long networkLayerId;

    /**
     * 宿主机共享存储路径
     */
    @TableField(value = "SHARED_STORAGE_PATH")
    @Schema(description="宿主机共享存储路径")
    private String sharedStoragePath;

    /**
     * 宿主机状态 (0-准备中、1-就绪、2-异常、3-停用)
     */
    @TableField(value = "STATE")
    @Schema(description="宿主机状态 (0-准备中、1-就绪、2-异常、3-停用)")
    private HostStateEnum state;

    /**
     * 宿主机状态消息
     */
    @TableField(value = "STATE_MESSAGE")
    @Schema(description="宿主机状态消息")
    private String stateMessage;

    /**
     * 虚拟 CPU 已定义核数
     */
    @TableField(value = "VIRTUAL_CPU_DEFINITION_NUMBER")
    @Schema(description = "虚拟 CPU 已定义核数")
    private Integer virtualCpuDefinitionNumber;

    /**
     * 虚拟 CPU 总核数
     */
    @TableField(value = "VIRTUAL_CPU_ALL_NUMBER")
    @Schema(description = "虚拟 CPU 总核数")
    private Integer virtualCpuAllNumber;

    /**
     * 逻辑删除
     */
    @TableField(value = "IS_DELETED")
    @Schema(description="逻辑删除")
    @TableLogic
    private Integer isDeleted;

    /**
     * 更新者
     */
    @TableField(value = "UPDATER")
    @Schema(description="更新者")
    private Long updater;

    /**
     * 创建者
     */
    @TableField(value = "CREATOR")
    @Schema(description="创建者")
    private Long creator;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    @Schema(description="更新时间")
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    @Schema(description="创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_NAME = "NAME";

    public static final String COL_DESCRIPTION = "DESCRIPTION";

    public static final String COL_IP = "IP";

    public static final String COL_MEMORY_SIZE = "MEMORY_SIZE";

    public static final String COL_CPU_NUMBER = "CPU_NUMBER";

    public static final String COL_ARCHITECTURE = "ARCHITECTURE";

    public static final String COL_LOGIN_USER = "LOGIN_USER";

    public static final String COL_LOGIN_PASSWORD = "LOGIN_PASSWORD";

    public static final String COL_SSH_PORT = "SSH_PORT";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";

    public static final String COL_NETWORK_LAYER_ID = "NETWORK_LAYER_ID";

    public static final String COL_SHARED_STORAGE_PATH = "SHARED_STORAGE_PATH";

    public static final String COL_STATE = "STATE";

    public static final String COL_STATE_MESSAGE = "STATE_MESSAGE";

    public static final String COL_VIRTUAL_CPU_ALL_NUMBER = "VIRTUAL_CPU_ALL_NUMBER";

    public static final String COL_VIRTUAL_CPU_DEFINITION_NUMBER = "VIRTUAL_CPU_DEFINITION_NUMBER";
}
