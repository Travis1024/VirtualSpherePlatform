package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.io.Serializable;

/**
 * @ClassName VmwareCreateFormEnum
 * @Description VmwareCreateFormEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Getter
public enum VmwareCreateFormEnum implements Serializable {
    ISO(1, "ISO安装介质"),
    IMAGE(2, "现有磁盘镜像")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    VmwareCreateFormEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VmwareCreateFormEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (VmwareCreateFormEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
