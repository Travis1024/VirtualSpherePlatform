package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName DynamicConfigTypeEnum
 * @Description DynamicConfigTypeEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Getter
public enum DynamicConfigTypeEnum {
    /**
     * 新增类型：关注链路处理
     * {@see org.travis.center.support.processor.AbstractDynamicConfigService#getMatchedService}
     */

    MONITOR_PERIOD(1, "监测周期配置"),
    UNIVERSAL(900, "通用配置"),
    UNIVERSAL_THRESHOLDS_CEILING(901, "通用配置-阈值上限"),
    UNIVERSAL_THRESHOLDS_FLOOR(902, "通用配置-阈值下限"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    DynamicConfigTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    public static boolean isUniversal(DynamicConfigTypeEnum dynamicConfigTypeEnum) {
        if (dynamicConfigTypeEnum == null) {
            return false;
        }
        return dynamicConfigTypeEnum.getValue() >= UNIVERSAL.getValue();
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DynamicConfigTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (DynamicConfigTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
