package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName DiskTypeEnum
 * @Description DiskTypeEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/22
 */
@Getter
public enum DiskTypeEnum {

    DATA(1, "数据磁盘"),
    ROOT(2, "系统磁盘")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    DiskTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DiskTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (DiskTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
