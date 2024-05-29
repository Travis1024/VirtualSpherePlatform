package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName ImageTypeEnum
 * @Description ImageTypeEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Getter
public enum ImageTypeEnum {

    ISO(1, "ISO-镜像"),
    SYSTEM(2, "系统镜像")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    ImageTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ImageTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ImageTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
