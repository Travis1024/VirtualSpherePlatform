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
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.enums.ScheduleGroupEnum;
import org.travis.center.common.enums.ScheduleStatusEnum;

/**
 * @ClassName ScheduleJob
 * @Description ScheduleJob
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_SCHEDULE_JOB")
public class ScheduleJob extends com.baomidou.mybatisplus.extension.activerecord.Model<ScheduleJob> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 定时任务名称
     */
    @TableField(value = "SCHEDULE_NAME")
    @Schema(description="定时任务名称")
    private String scheduleName;

    /**
     * Crontab 表达式
     */
    @TableField(value = "CRON_EXPRESSION")
    @Schema(description="Crontab 表达式")
    private String cronExpression;

    /**
     * Crontab 表达式描述信息
     */
    @TableField(value = "CRON_DESCRIPTION")
    @Schema(description="Crontab 表达式描述信息")
    private String cronDescription;

    /**
     * 是否不可修改（0-可修改、1-禁止修改）
     */
    @TableField(value = "IS_FIXED")
    @Schema(description="是否不可修改（0-可修改、1-禁止修改）")
    private IsFixedEnum isFixed;

    /**
     * 定时任务状态（0-启动、1-停止）
     */
    @TableField(value = "SCHEDULE_STATUS")
    @Schema(description="定时任务状态（0-启动、1-停止）")
    private ScheduleStatusEnum scheduleStatus;

    /**
     * 定时任务实现类
     */
    @TableField(value = "JOB_CLASS")
    @Schema(description="定时任务实现类")
    private String jobClass;

    /**
     * 定时任务分组
     */
    @TableField(value = "JOB_GROUP")
    @Schema(description="定时任务分组")
    private ScheduleGroupEnum jobGroup;

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

    public static final String COL_SCHEDULE_NAME = "SCHEDULE_NAME";

    public static final String COL_CRON_EXPRESSION = "CRON_EXPRESSION";

    public static final String COL_CRON_DESCRIPTION = "CRON_DESCRIPTION";

    public static final String COL_IS_FIXED = "IS_FIXED";

    public static final String COL_SCHEDULE_STATUS = "SCHEDULE_STATUS";

    public static final String COL_JOB_CLASS = "JOB_CLASS";

    public static final String COL_JOB_GROUP = "JOB_GROUP";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
