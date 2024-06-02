package org.travis.center.common.entity.support;

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
 * @ClassName CrontabInfo
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_CRONTAB_INFO")
public class CrontabInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<CrontabInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 定时任务 CRON 表达式
     */
    @TableField(value = "CRON_EXPRESSION")
    @Schema(description="定时任务 CRON 表达式")
    private String cronExpression;

    /**
     * CRON 表达式描述信息
     */
    @TableField(value = "CRON_DESCRIPTION")
    @Schema(description="CRON 表达式描述信息")
    private String cronDescription;

    /**
     * 定时任务名称
     */
    @TableField(value = "CRON_NAME")
    @Schema(description="定时任务名称")
    private String cronName;

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

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_CRON_EXPRESSION = "CRON_EXPRESSION";

    public static final String COL_CRON_DESCRIPTION = "CRON_DESCRIPTION";

    public static final String COL_CRON_NAME = "CRON_NAME";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
