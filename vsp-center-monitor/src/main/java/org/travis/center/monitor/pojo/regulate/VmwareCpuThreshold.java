package org.travis.center.monitor.pojo.regulate;

import lombok.Data;

/**
 * @ClassName VmwareCpuThreshold
 * @Description VmwareCpuThreshold
 * @Author Travis
 * @Data 2024/10
 */
@Data
public class VmwareCpuThreshold {
    private Integer thresholdCpuHighValue;
    private Integer thresholdCpuHighTimeRange;
    private Double thresholdCpuHighTimeRangeRate;
    private Integer thresholdCpuLowValue;
    private Integer thresholdCpuLowTimeRange;
    private Double thresholdCpuLowTimeRangeRate;
}
