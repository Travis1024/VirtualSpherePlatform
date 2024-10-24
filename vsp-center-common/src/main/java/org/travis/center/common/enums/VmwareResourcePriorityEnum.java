package org.travis.center.common.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * @ClassName ResourcePriorityEnum
 * @Description ResourcePriorityEnum
 * @Author Travis
 * @Data 2024/10
 */
@Getter
public enum VmwareResourcePriorityEnum implements Serializable {
    HIGH("高优先级"),
    MIDDLE("中优先级"),
    LOW("低优先级"),
    ;

    private final String desc;

    VmwareResourcePriorityEnum(String desc) {
        this.desc = desc;
    }
}
