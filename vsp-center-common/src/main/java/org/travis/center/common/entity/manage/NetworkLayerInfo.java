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
 * @ClassName NetworkLayerInfo
 * @Description TODO
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
@TableName(value = "VSP_NETWORK_LAYER_INFO")
public class NetworkLayerInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<NetworkLayerInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 网卡名称（eg：p4p1）
     */
    @TableField(value = "NIC_NAME")
    @Schema(description="网卡名称（eg：p4p1）")
    private String nicName;

    /**
     * 网卡起始 IP 地址（192.168.0.0）
     */
    @TableField(value = "NIC_START_ADDRESS")
    @Schema(description="网卡起始 IP 地址（192.168.0.0）")
    private String nicStartAddress;

    /**
     * 网卡掩码（eg：24）
     */
    @TableField(value = "NIC_MASK")
    @Schema(description="网卡掩码（eg：24）")
    private Integer nicMask;

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

    public static final String COL_NIC_NAME = "NIC_NAME";

    public static final String COL_NIC_START_ADDRESS = "NIC_START_ADDRESS";

    public static final String COL_NIC_MASK = "NIC_MASK";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
