package org.travis.center.support.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName QuartzCreateParamDTO
 * @Description Quartz任务添加请求参数
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Data
public class QuartzCreateParamDTO implements Serializable {

    @NotBlank(message = "任务类不能为空")
    @Schema(description = "任务类路径")
    private String jobClazz;

    @NotBlank(message = "任务类名不能为空")
    @Schema(description = "任务类名")
    private String jobName;

    /**
     * 「jobGroup」+「jobName」组成唯一标识，所以如果这个参数为空，那么默认以任务类 key 作为组名
     */
    @Schema(description = "任务组名")
    private String jobGroup;

    @Schema(description = "任务数据")
    private Map<String, Object> jobDataMap;

    @Schema(description = "任务描述")
    private String jobDescription;

    @Schema(description = "触发器 crontab 表达式")
    private String triggerCrontab;
}
