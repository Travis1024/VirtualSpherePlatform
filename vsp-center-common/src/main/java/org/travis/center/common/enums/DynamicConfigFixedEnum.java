package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName DynamicConfigFixedEnum
 * @Description 动态配置是否允许修改枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/12
 */
@Getter
public enum DynamicConfigFixedEnum {
    ALLOW_UPDATE(0, "允许修改"),
    DISALLOW_UPDATE(1, "禁止修改"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    DynamicConfigFixedEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DynamicConfigFixedEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (DynamicConfigFixedEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
