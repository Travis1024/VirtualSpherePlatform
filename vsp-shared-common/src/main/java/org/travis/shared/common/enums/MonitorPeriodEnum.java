package org.travis.shared.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName MonitorPeriodEnum
 * @Description 虚拟机监测周期枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/26
 */
@Getter
@AllArgsConstructor
public enum MonitorPeriodEnum {

    MONITOR_PERIOD_1_SECONDS(1, "1s"),
    MONITOR_PERIOD_2_SECONDS(2, "2s"),
    MONITOR_PERIOD_3_SECONDS(3, "3s"),
    MONITOR_PERIOD_5_SECONDS(5, "5s"),
    MONITOR_PERIOD_8_SECONDS(8, "8s"),
    MONITOR_PERIOD_10_SECONDS(10, "10s"),
    MONITOR_PERIOD_15_SECONDS(15, "15s"),
    MONITOR_PERIOD_20_SECONDS(20, "20s"),
    MONITOR_PERIOD_30_SECONDS(30, "30s"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MonitorPeriodEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (MonitorPeriodEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
