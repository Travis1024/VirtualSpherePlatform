package org.travis.center.support.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.Tuple;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.travis.shared.common.enums.MonitorPeriodEnum;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DynamicConfigPeriodUtil
 * @Description 监控周期队列缓存依赖类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/26
 */
@Slf4j
@Component
public class DynamicConfigPeriodUtil {

    @Resource
    public Cache<String, Object> commonPermanentCache;

    @SuppressWarnings("unchecked")
    public void put(MonitorPeriodEnum monitorPeriodEnum, String vmwareUuid) {
        synchronized (getPeriodLock(monitorPeriodEnum)) {
            Optional<Object> objectOptional = Optional.ofNullable(commonPermanentCache.getIfPresent(monitorPeriodEnum.getDisplay()));
            if (objectOptional.isPresent()) {
                Set<String> vmwareUuids = (Set<String>) objectOptional.get();
                vmwareUuids.add(vmwareUuid);
                // TODO 判断是否需要 put
            } else {
                Set<String> vmwareUuids = new HashSet<>();
                vmwareUuids.add(vmwareUuid);
                commonPermanentCache.put(monitorPeriodEnum.getDisplay(), vmwareUuids);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> get(MonitorPeriodEnum monitorPeriodEnum) {
        synchronized (getPeriodLock(monitorPeriodEnum)) {
            Set<String> vmwareUuids = (Set<String>) Optional.ofNullable(commonPermanentCache.getIfPresent(monitorPeriodEnum.getDisplay())).orElse(Collections.EMPTY_SET);
            return new ArrayList<>(vmwareUuids);
        }
    }

    public static String getPeriodLock(MonitorPeriodEnum monitorPeriodEnum) {
        return "periodLock:" + monitorPeriodEnum.getDisplay();
    }
}
