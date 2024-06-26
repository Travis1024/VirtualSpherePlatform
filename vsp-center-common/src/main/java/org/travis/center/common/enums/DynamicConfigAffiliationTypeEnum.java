package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName DynamicConfigAffiliationTypeEnum
 * @Description DynamicConfigAffiliationTypeEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Getter
@AllArgsConstructor
public enum DynamicConfigAffiliationTypeEnum {

    SYSTEM(0, "系统配置"),
    HOST(1, "宿主机配置"),
    VMWARE(2, "虚拟机配置")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DynamicConfigAffiliationTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (DynamicConfigAffiliationTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
