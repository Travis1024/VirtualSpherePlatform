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

    MONITOR(1, "指标监控配置"),
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
