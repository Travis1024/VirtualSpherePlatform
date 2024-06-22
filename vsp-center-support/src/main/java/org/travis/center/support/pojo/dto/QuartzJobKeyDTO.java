package org.travis.center.support.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName QuartzJobKeyDTO
 * @Description Quartz 任务定位参数
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Data
public class QuartzJobKeyDTO implements Serializable {
    @NotBlank(message = "任务类名不能为空")
    @Schema(description = "任务类名")
    private String jobName;

    @NotBlank(message = "任务组名不能为空")
    @Schema(description = "任务组名-命名空间")
    private String jobGroup;
}
