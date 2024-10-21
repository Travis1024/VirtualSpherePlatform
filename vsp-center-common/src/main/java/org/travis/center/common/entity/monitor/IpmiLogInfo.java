package org.travis.center.common.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @ClassName IpmiLogInfo
 * @Description IpmiLogInfo
 * @Author Travis
 * @Data 2024/10
 */
/**
 * Ipmi日志信息表
 */
@Schema(description="Ipmi日志信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_IPMI_LOG_INFO")
public class IpmiLogInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<IpmiLogInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "IPMI_NAME")
    @Schema(description="名称")
    private String ipmiName;

    /**
     * IP地址
     */
    @TableField(value = "IPMI_IP")
    @Schema(description="IP地址")
    private String ipmiIp;

    /**
     * 端口号
     */
    @TableField(value = "IPMI_PORT")
    @Schema(description="端口号")
    private String ipmiPort;

    /**
     * 时间戳
     */
    @TableField(value = "IPMI_TIMESTAMP")
    @Schema(description="时间戳")
    private Long ipmiTimestamp;

    /**
     * 数据
     */
    @TableField(value = "IPMI_DATA")
    @Schema(description="数据")
    private String ipmiData;

    /**
     * 逻辑删除
     */
    @TableField(value = "IS_DELETED")
    @Schema(description="逻辑删除")
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

    public static final String COL_ID = "ID";

    public static final String COL_IPMI_NAME = "IPMI_NAME";

    public static final String COL_IPMI_IP = "IPMI_IP";

    public static final String COL_IPMI_PORT = "IPMI_PORT";

    public static final String COL_IPMI_TIMESTAMP = "IPMI_TIMESTAMP";

    public static final String COL_IPMI_DATA = "IPMI_DATA";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
