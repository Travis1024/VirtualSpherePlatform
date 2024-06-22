package org.travis.center.support.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName QuartzTriggerDetailVO
 * @Description Quartz定时任务触发器详情类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Data
public class QuartzTriggerDetailVO implements Serializable {
    @Schema(description ="触发器 Crontab 表达式")
    private String triggerCrontab;

    @Schema(description = "触发器状态")
    private String triggerState;

    @Schema(description = "最近触发时间列表")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private List<Date> recentFireTimeList;
}
