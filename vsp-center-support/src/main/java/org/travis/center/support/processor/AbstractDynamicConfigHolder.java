package org.travis.center.support.processor;

import org.travis.center.common.enums.DynamicConfigTypeEnum;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractDynamicConfigHolder
 * @Description 抽动态配置处理器持有类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/2
 */
public class AbstractDynamicConfigHolder {

    private static final Map<DynamicConfigTypeEnum, AbstractDynamicConfigService> DYNAMIC_CONFIG_HOLDER_MAP = new ConcurrentHashMap<>(8);

    public static AbstractDynamicConfigService getDynamicConfigHandler(DynamicConfigTypeEnum dynamicConfigTypeEnum) {
        return Optional.ofNullable(DYNAMIC_CONFIG_HOLDER_MAP.get(dynamicConfigTypeEnum)).orElse(DYNAMIC_CONFIG_HOLDER_MAP.get(DynamicConfigTypeEnum.UNIVERSAL));
    }

    public static void addDynamicConfigHandler(DynamicConfigTypeEnum dynamicConfigTypeEnum, AbstractDynamicConfigService abstractDynamicConfigService) {
        DYNAMIC_CONFIG_HOLDER_MAP.put(dynamicConfigTypeEnum, abstractDynamicConfigService);
    }
}
