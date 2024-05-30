package org.travis.center.common.enums;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @ClassName MsgModuleEnum
 * @Description MsgModuleEnum
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Getter
public enum MsgModuleEnum {

    SYSTEM(0, "系统模块消息"),
    HOST(1, "宿主机模块消息"),
    VMWARE(2, "虚拟机模块消息"),
    MONITOR(3, "集群监控模块消息"),
    RESOURCE(4, "资源调控模块消息"),
    SERVICE(5, "服务质量模块消息"),
    SCRIPT(6, "测试脚本模块消息")
    ;

    @EnumValue
    private final Integer value;
    @JsonValue
    private final String display;

    MsgModuleEnum(Integer value, String display) {
        this.display = display;
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MsgModuleEnum of(String display) {
        if (StrUtil.isEmpty(display)) {
            return null;
        }
        for (MsgModuleEnum anEnum : values()) {
            if (anEnum.getDisplay().equals(display)) {
                return anEnum;
            }
        }
        return null;
    }
}
