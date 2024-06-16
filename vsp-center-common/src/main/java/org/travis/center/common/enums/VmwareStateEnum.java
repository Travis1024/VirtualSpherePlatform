package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;

/**
 * @ClassName VmwareStateEnum
 * @Description VmwareStateEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Getter
public enum VmwareStateEnum implements Serializable {
    // Official status
    UN_DEFINED(0, "undefined", "未定义"),
    SHUT_OFF(1, "shut off", "关闭"),
    RUNNING(2, "running", "运行"),
    PAUSED(3, "paused", "暂停"),
    SAVED(4, "saved", "保存"),
    CRASHED(5, "crashed", "崩溃"),

    // Intermediate statue
    ING_CREATE(10, "creating", "创建中"),
    ING_START(11, "starting", "启动中"),

    // Custom state
    UNKNOW(20, "unknow", "未知"),
    ERROR(21, "error", "异常"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;
    private final String tag;

    VmwareStateEnum(Integer value, String tag, String display) {
        this.value = value;
        this.tag = tag;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VmwareStateEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (VmwareStateEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }

    public static VmwareStateEnum ofTag(String tag) {
        if (StrUtil.isEmpty(tag)) {
            return null;
        }
        for (VmwareStateEnum anEnum : values()) {
            if (anEnum.getTag().equals(tag)) {
                return anEnum;
            }
        }
        return null;
    }
}
