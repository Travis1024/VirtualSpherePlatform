package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;

/**
 * @ClassName HostStateEnum
 * @Description HostStateEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Getter
public enum HostStateEnum implements Serializable {

    IN_PREPARATION(0, "准备中"),
    READY(1, "就绪"),
    INIT_ERROR(2, "初始化异常"),
    DISABLE(3, "停用"),
    HEART_BEAT_ERROR(4, "心跳异常")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    HostStateEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static HostStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (HostStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
