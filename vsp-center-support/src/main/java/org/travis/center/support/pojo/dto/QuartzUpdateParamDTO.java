package org.travis.center.support.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName QuartzUpdateParamDTO
 * @Description Quartz任务更新请求参数
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Data
public class QuartzUpdateParamDTO implements Serializable {
    @NotBlank(message = "任务类名不能为空")
    @Schema(description = "任务类名")
    private String jobName;

    @NotBlank(message = "任务组名不能为空")
    @Schema(description = "任务组名-命名空间")
    private String jobGroup;

    @NotBlank(message = "触发器 crontab 表达式不能为空")
    @Schema(description = "触发器 crontab 表达式")
    private String triggerCrontab;
}
