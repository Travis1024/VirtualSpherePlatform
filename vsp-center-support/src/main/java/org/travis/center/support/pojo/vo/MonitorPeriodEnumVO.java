package org.travis.center.support.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MonitorPeriodEnumVO
 * @Description 虚拟机监测周期枚举类 VO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MonitorPeriodEnumVO {
    @Schema(description = "监控周期参数值")
    private Integer value;
    @Schema(description = "监控周期描述")
    private String display;
}
