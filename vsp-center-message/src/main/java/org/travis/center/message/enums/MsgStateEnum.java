package org.travis.center.message.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName MsgStateEnum
 * @Description MsgStateEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Getter
public enum MsgStateEnum {

    INFO(1, "普通消息"),
    WARNING(2, "告警消息"),
    ERROR(3, "异常消息")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    MsgStateEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MsgStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (MsgStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }

}
