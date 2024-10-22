package org.travis.shared.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName ServiceStateEnum
 * @Description ServiceStateEnum
 * @Author Travis
 * @Data 2024/10
 */
@Getter
public enum ServiceStateEnum {

    ENABLE("enable", "启用"),
    DISABLE("disable", "禁用"),
    ;

    @EnumValue
    private final String value;
    @JsonValue
    private final String display;

    ServiceStateEnum(String value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ServiceStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ServiceStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
