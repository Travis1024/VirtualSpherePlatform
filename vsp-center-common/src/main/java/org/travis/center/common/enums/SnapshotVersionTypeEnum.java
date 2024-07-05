package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName SnapshotVersionTypeEnum
 * @Description 快照版本类型枚举
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/5
 */
@AllArgsConstructor
@Getter
public enum SnapshotVersionTypeEnum {

    ORIGINAL_VERSION(0, "原始版本"),
    ADDITIONAL_VERSION(1, "附加版本"),
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SnapshotVersionTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (SnapshotVersionTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
