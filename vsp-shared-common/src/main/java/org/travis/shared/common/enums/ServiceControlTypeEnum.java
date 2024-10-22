package org.travis.shared.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName ServiceControlTypeEnum
 * @Description ServiceControlTypeEnum
 * @Author Travis
 * @Data 2024/10
 */
@Getter
public enum ServiceControlTypeEnum {

    SEMI_AUTOMATIC(1, "半自动"),
    AUTOMATIC(2, "自动"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    ServiceControlTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ServiceControlTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ServiceControlTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
