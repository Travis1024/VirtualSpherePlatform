package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName BusinessTypeEnum
 * @Description BusinessTypeEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Getter
public enum BusinessTypeEnum {

    OTHER(1, "其他"),
    INSERT(2, "新增"),
    UPDATE(3, "修改"),
    DELETE(4, "删除"),
    QUERY(5, "查询")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    BusinessTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static BusinessTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (BusinessTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
