package org.travis.center.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName UserRoleEnum
 * @Description UserRoleEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Getter
public enum UserRoleEnum {
    ADMIN_USER(1, "管理员用户"),
    NORMAL_USER(2, "普通用户")
    ;

    @EnumValue
    private Integer value;
    @JsonValue
    private String display;

    UserRoleEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UserRoleEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserRoleEnum anEnum : values()) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
