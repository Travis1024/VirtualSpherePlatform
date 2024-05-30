package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName MsgConfirmEnum
 * @Description MsgConfirmEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Getter
public enum MsgConfirmEnum {

    UN_CONFIRMED(0, "未确认"),
    CONFIRMED(1, "已确认"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    MsgConfirmEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MsgConfirmEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (MsgConfirmEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
