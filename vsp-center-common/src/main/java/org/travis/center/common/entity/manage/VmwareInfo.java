package org.travis.center.common.entity.manage;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName VmwareInfo
 * @Description VmwareInfo
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_VMWARE_INFO")
public class VmwareInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<VmwareInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 虚拟机名称
     */
    @TableField(value = "\"NAME\"")
    @Schema(description="虚拟机名称")
    private String name;

    /**
     * 虚拟机 UUID-自定义
     */
    @TableField(value = "UUID")
    @Schema(description="虚拟机 UUID-自定义")
    private String uuid;

    /**
     * 虚拟机描述信息
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="虚拟机描述信息")
    private String description;

    /**
     * 虚拟机当前所属宿主机 ID
     */
    @TableField(value = "HOST_ID")
    @Schema(description="虚拟机当前所属宿主机 ID")
    private Long hostId;

    /**
     * 虚拟机当前状态
     */
    @TableField(value = "\"STATE\"")
    @Schema(description="虚拟机当前状态")
    private Integer state;

    /**
     * 虚拟机创建形式（1-ISO安装介质、2-现有磁盘镜像）
     */
    @TableField(value = "CREATE_FORM")
    @Schema(description="虚拟机创建形式（1-ISO安装介质、2-现有磁盘镜像）")
    private Integer createForm;

    /**
     * 虚拟机 vCPU 最大数量
     */
    @TableField(value = "VCPU_MAX")
    @Schema(description="虚拟机 vCPU 最大数量")
    private Integer vcpuMax;

    /**
     * 虚拟机 vCPU 当前数量
     */
    @TableField(value = "VCPU_CURRENT")
    @Schema(description="虚拟机 vCPU 当前数量")
    private Integer vcpuCurrent;

    /**
     * 虚拟机内存最大值（KB）
     */
    @TableField(value = "MEMORY_MAX")
    @Schema(description="虚拟机内存最大值（KB）")
    private Long memoryMax;

    /**
     * 虚拟机内存当前值（KB）
     */
    @TableField(value = "MEMORY_CURRENT")
    @Schema(description="虚拟机内存当前值（KB）")
    private Long memoryCurrent;

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

    public static final String COL_UUID = "UUID";

    public static final String COL_DESCRIPTION = "DESCRIPTION";

    public static final String COL_HOST_ID = "HOST_ID";

    public static final String COL_STATE = "STATE";

    public static final String COL_CREATE_FORM = "CREATE_FORM";

    public static final String COL_VCPU_MAX = "VCPU_MAX";

    public static final String COL_VCPU_CURRENT = "VCPU_CURRENT";

    public static final String COL_MEMORY_MAX = "MEMORY_MAX";

    public static final String COL_MEMORY_CURRENT = "MEMORY_CURRENT";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
