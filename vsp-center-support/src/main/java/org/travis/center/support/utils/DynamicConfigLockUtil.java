package org.travis.center.support.utils;

import lombok.extern.slf4j.Slf4j;
/**
 * @ClassName DynamicConfigLockUtil
 * @Description 动态配置锁工具类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/26
 */
@Slf4j
public class DynamicConfigLockUtil {

    public static String getVmwarePeriodMapLock(String vmwareUuid) {
        return "vmwarePeriodMapLock:" + vmwareUuid;
    }

    public static String getConfigLock(Long configId) {
        return "configLock:" + configId;
    }
}
