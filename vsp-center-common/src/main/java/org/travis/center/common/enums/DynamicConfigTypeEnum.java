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

    OTHER(0, "其他配置"),
    MONITOR_PERIOD(1, "监测周期配置"),
    THRESHOLD(2, "阈值配置"),

    /**
     * 不要使用（对齐配置项）
     */
    UNIVERSAL(999, "通用配置")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    DynamicConfigTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
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
