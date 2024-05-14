package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName ImageArchEnum
 * @Description ImageArchEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Getter
public enum ImageArchEnum {

    X86_64(1, "x86_64"),
    AARCH64(2, "AArch64")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    ImageArchEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ImageArchEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ImageArchEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
