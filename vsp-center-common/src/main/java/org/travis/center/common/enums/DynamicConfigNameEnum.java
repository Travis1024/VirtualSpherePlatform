package org.travis.center.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName DynamicConfigNameEnum
 * @Description 动态配置名称枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/27
 */
@Getter
@AllArgsConstructor
public enum DynamicConfigNameEnum {

    VMWARE_MONITOR_PERIOD_SECONDS("数据监测周期(单位-秒)"),
    ;

    private final String display;
}
