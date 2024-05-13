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

/**
 * @ClassName DiskInfo
 * @Description DiskInfo
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
@TableName(value = "VSP_DISK_INFO")
public class DiskInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<DiskInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 磁盘名称
     */
    @TableField(value = "\"NAME\"")
    @Schema(description="磁盘名称")
    private String name;

    /**
     * 磁盘描述信息
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="磁盘描述信息")
    private String description;

    /**
     * 磁盘大小（字节）
     */
    @TableField(value = "SPACE_SIZE")
    @Schema(description="磁盘大小（字节）")
    private Long spaceSize;

    /**
     * 磁盘存放路径（共享存储子路径）
     */
    @TableField(value = "SUB_PATH")
    @Schema(description="磁盘存放路径（共享存储子路径）")
    private String subPath;

    /**
     * 磁盘所属虚拟机ID
     */
    @TableField(value = "VMWARE_ID")
    @Schema(description="磁盘所属虚拟机ID")
    private Long vmwareId;

    /**
     * 磁盘类型（1-Data、2-Root）
     */
    @TableField(value = "DISK_TYPE")
    @Schema(description="磁盘类型（1-Data、2-Root）")
    private Integer diskType;

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

    public static final String COL_SPACE_SIZE = "SPACE_SIZE";

    public static final String COL_SUB_PATH = "SUB_PATH";

    public static final String COL_VMWARE_ID = "VMWARE_ID";

    public static final String COL_DISK_TYPE = "DISK_TYPE";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
