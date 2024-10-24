package org.travis.center.monitor.pojo.regulate;

import lombok.Data;

/**
 * @ClassName VmwareBasicThreshold
 * @Description VmwareBasicThreshold
 * @Author Travis
 * @Data 2024/10
 */
@Data
public class VmwareBasicThreshold {
    private Integer basicDataMonitorPeriod;
    private Integer basicRegulateMinTimeInterval;
    private Integer basicRegulateRecommendMessageMinTimeInterval;
    private String basicResourcePriorityType;
    private String basicRegulateStrategyType;
}
