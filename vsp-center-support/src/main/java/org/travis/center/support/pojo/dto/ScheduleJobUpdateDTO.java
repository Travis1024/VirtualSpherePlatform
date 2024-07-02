package org.travis.center.support.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName ScheduleJobCreateDTO
 * @Description ScheduleJobCreateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Data
public class ScheduleJobUpdateDTO implements Serializable {
    /**
     * ID
     */
    @RequestLockKey
    @Schema(description="ID")
    @NotNull(message = "ID不能为空")
    private Long id;

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
}
