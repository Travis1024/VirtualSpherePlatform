package org.travis.center.support.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.enums.ScheduleGroupEnum;
import org.travis.center.common.enums.ScheduleStatusEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName ScheduleJobCreateDTO
 * @Description ScheduleJobCreateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Data
public class ScheduleJobCreateDTO implements Serializable {
    /**
     * 定时任务名称
     */
    @Schema(description="定时任务名称")
    @NotBlank(message = "定时任务名称不能为空")
    private String scheduleName;

    /**
     * Crontab 表达式
     */
    @Schema(description="Crontab 表达式")
    @NotBlank(message = "Crontab 表达式不能为空")
    private String cronExpression;

    /**
     * Crontab 表达式描述信息
     */
    @Schema(description="Crontab 表达式描述信息")
    @NotBlank(message = "Crontab 表达式描述信息不能为空")
    private String cronDescription;

    /**
     * 是否不可修改（0-可修改、1-禁止修改）
     */
    @Schema(description="是否不可修改（0-可修改、1-禁止修改）")
    @NotNull(message = "修改能力不能为空")
    private IsFixedEnum isFixed;

    /**
     * 定时任务实现类
     */
    @Schema(description="定时任务实现类")
    @NotBlank(message = "定时任务实现类不能为空")
    private String jobClass;

    /**
     * 定时任务分组
     */
    @Schema(description="定时任务分组")
    @NotNull(message = "定时任务分组不能为空")
    private ScheduleGroupEnum jobGroup;

    @Schema(description = "任务数据")
    private Map<String, Object> jobDataMap;
}
