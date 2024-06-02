package org.travis.center.support.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName CrontabUpdateDTO
 * @Description CrontabUpdateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@Data
public class CrontabUpdateDTO implements Serializable {
    @Schema(description="ID")
    @NotNull(message = "定时任务ID不能为空!")
    private Long id;

    @Schema(description="定时任务 CRON 表达式")
    @NotBlank(message = "定时任务 CRON 表达式不能为空!")
    private String cronExpression;
}
