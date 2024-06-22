package org.travis.center.support.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName QuartzJobDetailVO
 * @Description Quartz定时任务详情类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Data
public class QuartzJobDetailVO implements Serializable {
    @Schema(description = "任务类路径")
    private String jobClazz;

    @Schema(description = "任务类名")
    private String jobName;

    @Schema(description = "任务组名-命名空间")
    private String jobGroup;

    @Schema(description = "任务数据")
    private Map<String, Object> jobDataMap;

    @Schema(description = "任务描述")
    private String jobDescription;

    @Schema(description = "触发器列表")
    private List<QuartzTriggerDetailVO> triggerDetailList;
}
