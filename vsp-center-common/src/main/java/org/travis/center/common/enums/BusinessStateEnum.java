package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName BusinessStateEnum
 * @Description BusinessStateEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Getter
public enum BusinessStateEnum {

    UNKNOW(0, "未知"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    BusinessStateEnum(Integer value, String display) {
        this.display = display;
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BusinessStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (BusinessStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
