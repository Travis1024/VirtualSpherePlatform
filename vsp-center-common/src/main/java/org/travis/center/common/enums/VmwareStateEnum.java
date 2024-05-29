package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;

/**
 * @ClassName VmwareStateEnum
 * @Description VmwareStateEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Getter
public enum VmwareStateEnum implements Serializable {
    IN_PREPARATION(0, "创建中"),
    STARTING(1, "启动中"),

    PAUSE(2, "暂停状态"),
    POWER_ON(3, "运行状态"),
    POWER_OFF(4, "关机状态"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    VmwareStateEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VmwareStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (VmwareStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
