package org.travis.shared.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName MachineTypeEnum
 * @Description MachineTypeEnum
 * @Author Travis
 * @Data 2024/10
 */
@Getter
public enum MachineTypeEnum {

    HOST(1, "宿主机"),
    VMWARE(2, "虚拟机")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    MachineTypeEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MachineTypeEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (MachineTypeEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
