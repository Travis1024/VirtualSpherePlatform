package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName DiskMountEnum
 * @Description DiskMountEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/23
 */
@Getter
public enum DiskMountEnum {

    UN_MOUNTED(0, "未挂载"),
    MOUNTED(1, "已挂载")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    DiskMountEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DiskMountEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (DiskMountEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
