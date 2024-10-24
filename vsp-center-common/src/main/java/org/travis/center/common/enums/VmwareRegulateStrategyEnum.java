package org.travis.center.common.enums;

import lombok.Getter;

/**
 * @ClassName VmwareRegulateStrategyEnum
 * @Description VmwareRegulateStrategyEnum
 * @Author Travis
 * @Data 2024/10
 */
@Getter
public enum VmwareRegulateStrategyEnum {
    AUTOMATIC("自动调控"),
    SEMI_AUTOMATIC("半自动调控"),
    MANUAL("手动调控"),
    ;

    private final String desc;

    VmwareRegulateStrategyEnum(String desc) {
        this.desc = desc;
    }
}
