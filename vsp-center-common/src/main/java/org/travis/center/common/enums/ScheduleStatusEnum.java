package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName ScheduleStatusEnum
 * @Description 定时任务状态枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Getter
public enum ScheduleStatusEnum {

    RUNNING(0, "启动"),
    STOPPING(1, "停止"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    ScheduleStatusEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ScheduleStatusEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ScheduleStatusEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
