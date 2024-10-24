package org.travis.center.monitor.pojo.regulate;

import lombok.Data;

/**
 * @ClassName VmwareMemoryThreshold
 * @Description VmwareMemoryThreshold
 * @Author Travis
 * @Data 2024/10
 */
@Data
public class VmwareMemoryThreshold {
    private Integer thresholdMemoryHighValue;
    private Integer thresholdMemoryHighTimeRange;
    private Double thresholdMemoryHighTimeRangeRate;
    private Integer thresholdMemoryLowValue;
    private Integer thresholdMemoryLowTimeRange;
    private Double thresholdMemoryLowTimeRangeRate;
}
