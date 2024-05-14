package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;

/**
 * @ClassName ImageStateEnum
 * @Description ImageStateEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Getter
public enum ImageStateEnum implements Serializable {

    UPLOADING(1, "上传中"),
    ERROR(2, "异常"),
    READY(3, "就绪")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    ImageStateEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ImageStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ImageStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
