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
import org.travis.center.common.enums.SnapshotVersionTypeEnum;
import org.travis.shared.common.pipeline.ProcessModel;

/**
 * @ClassName SnapshotInfo
 * @Description SnapshotInfo
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/5
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_SNAPSHOT_INFO")
public class SnapshotInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<SnapshotInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 快照名称（用户定义）
     */
    @TableField(value = "SNAPSHOT_NAME")
    @Schema(description="快照名称（用户定义）")
    private String snapshotName;

    /**
     * 所属虚拟机 ID
     */
    @TableField(value = "VMWARE_ID")
    @Schema(description="所属虚拟机 ID")
    private Long vmwareId;

    /**
     * 所属虚拟机 UUID
     */
    @TableField(value = "VMWARE_UUID")
    @Schema(description="所属虚拟机 UUID")
    private String vmwareUuid;

    /**
     * 快照描述信息
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="快照描述信息")
    private String description;

    /**
     * 快照版本类型（0-原始快照）
     */
    @TableField(value = "VERSION_TYPE")
    @Schema(description="快照版本类型（0-原始快照）")
    private SnapshotVersionTypeEnum versionType;

    /**
     * 快照共享存储子路径
     */
    @TableField(value = "SUB_PATH")
    @Schema(description="快照共享存储子路径")
    private String subPath;

    /**
     * 快照名称（自动定义）
     */
    @TableField(value = "AUTO_SNAPSHOT_NAME")
    @Schema(description="快照名称（自动定义）")
    private String autoSnapshotName;

    /**
     * 快照目标设备（eg:vda）
     */
    @TableField(value = "TARGET_DEV")
    @Schema(description="快照目标设备（eg:vda）")
    private String targetDev;

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

    public static final String COL_SNAPSHOT_NAME = "SNAPSHOT_NAME";

    public static final String COL_VMWARE_ID = "VMWARE_ID";

    public static final String COL_VMWARE_UUID = "VMWARE_UUID";

    public static final String COL_DESCRIPTION = "DESCRIPTION";

    public static final String COL_VERSION_TYPE = "VERSION_TYPE";

    public static final String COL_SUB_PATH = "SUB_PATH";

    public static final String COL_AUTO_SNAPSHOT_NAME = "AUTO_SNAPSHOT_NAME";

    public static final String COL_TARGET_DEV = "TARGET_DEV";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
