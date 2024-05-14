package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName ImagePlatformEnum
 * @Description ImagePlatformEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Getter
public enum ImagePlatformEnum {

    OTHER(0, "Other"),
    LINUX(1, "Linux"),
    WINDOWS(2, "Windows")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    ImagePlatformEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ImagePlatformEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ImagePlatformEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
