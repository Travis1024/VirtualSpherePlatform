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
public enum ScheduleGroupEnum {

    UNKNOWN(0, "未知", "unknown"),
    VMWARE(1, "虚拟机任务组", "vmware"),
    HOST(2, "宿主机任务组", "host"),
    DATABASE(3, "数据库任务组", "database"),
    SYSTEM(9, "系统任务组", "system"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;
    private final String tag;

    ScheduleGroupEnum(Integer value, String display, String tag) {
        this.value = value;
        this.display = display;
        this.tag = tag;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ScheduleGroupEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (ScheduleGroupEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
