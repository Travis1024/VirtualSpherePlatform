package org.travis.center.support.processor;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigTypeEnum;
import org.travis.center.support.utils.DynamicConfigLockUtil;
import org.travis.shared.common.enums.MonitorPeriodEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName MonitorPeriodDynamicConfigService
 * @Description 「监测周期」动态配置服务类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/2
 */
@Service
public class MonitorPeriodDynamicConfigService extends AbstractDynamicConfigService {

    public MonitorPeriodDynamicConfigService() {
        dynamicConfigTypeEnum = DynamicConfigTypeEnum.MONITOR_PERIOD;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public void updateConfigValue(String configValue) {
        // 0.临时保存旧配置参数 + 校验新配置参数
        String oldConfigValue = dynamicConfigInfo.getConfigValue();
        MonitorPeriodEnum newMonitorPeriodEnum = Optional.ofNullable(MonitorPeriodEnum.ofValue(Integer.valueOf(configValue))).orElseThrow(() -> new RuntimeException("配置参数不合法"));

        // 1.更新持久化配置
        dynamicConfigInfoMapper.update(
                Wrappers.<DynamicConfigInfo>lambdaUpdate()
                        .set(DynamicConfigInfo::getConfigValue, configValue)
                        .eq(DynamicConfigInfo::getId, dynamicConfigInfo.getId())
        );
        // 2.删除缓存
        configPermanentCache.invalidate(dynamicConfigInfo.getId());

        // 3.从旧缓存队列中删除配置
        MonitorPeriodEnum oldMonitorPeriodEnum = MonitorPeriodEnum.ofValue(Integer.valueOf(oldConfigValue));
        Optional<Object> oldObjectOptional = Optional.ofNullable(commonPermanentCache.getIfPresent(oldMonitorPeriodEnum.getDisplay()));
        if (oldObjectOptional.isPresent()) {
            ConcurrentHashSet<String> vmwareUuids = (ConcurrentHashSet<String>) oldObjectOptional.get();
            vmwareUuids.remove(dynamicConfigInfo.getAffiliationMachineUuid());
            // TODO 判断 Set 是否需要重新 put 到缓存中
            // commonPermanentCache.put(monitorPeriodEnum.getDisplay(), vmwareUuids);
        }

        // 4.向新缓存队列中添加配置 <监测周期display, 「虚拟机UUID」Set>
        Optional<Object> newObjectOptional = Optional.ofNullable(commonPermanentCache.getIfPresent(newMonitorPeriodEnum.getDisplay()));
        if (newObjectOptional.isPresent()) {
            // 4.1.存在，则向监测周期队列中添加「虚拟机UUID」
            ConcurrentHashSet<String> vmwareUuids = (ConcurrentHashSet<String>) newObjectOptional.get();
            vmwareUuids.add(dynamicConfigInfo.getAffiliationMachineUuid());
            // TODO 判断 Set 是否需要重新 put 到缓存中
            // commonPermanentCache.put(monitorPeriodEnum.getDisplay(), vmwareUuids);
        } else {
            // 4.2.不存在，则创建监测周期 Set，添加「虚拟机UUID」
            ConcurrentHashSet<String> vmwareUuids = new ConcurrentHashSet<>();
            vmwareUuids.add(dynamicConfigInfo.getAffiliationMachineUuid());
            commonPermanentCache.put(newMonitorPeriodEnum.getDisplay(), vmwareUuids);
        }

        // 5.新增「虚拟机UUID」与「监测周期 display」映射
        commonPermanentCache.put(dynamicConfigInfo.getAffiliationMachineUuid(), newMonitorPeriodEnum.getDisplay());
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public void insertConfigValue() {
        synchronized (DynamicConfigLockUtil.getVmwarePeriodMapLock(String.valueOf(dynamicConfigInfo.getAffiliationMachineId()))) {
            // 1.持久化到数据库中
            dynamicConfigInfoMapper.insert(dynamicConfigInfo);
            // 2.缓存到 Caffeine
            configPermanentCache.put(dynamicConfigInfo.getId(), dynamicConfigInfo.getConfigValue());

            // 3.向缓存队列中添加配置 <监测周期display, 「虚拟机UUID」Set>
            // 3.1.解析当前动态配置-监测周期
            MonitorPeriodEnum monitorPeriodEnum = MonitorPeriodEnum.ofValue(Integer.valueOf(dynamicConfigInfo.getConfigValue()));
            Optional<Object> objectOptional = Optional.ofNullable(commonPermanentCache.getIfPresent(monitorPeriodEnum.getDisplay()));

            if (objectOptional.isPresent()) {
                // 3.2.1.存在，则向监测周期队列中添加虚拟机 UUID
                ConcurrentHashSet<String> vmwareUuids = (ConcurrentHashSet<String>) objectOptional.get();
                vmwareUuids.add(dynamicConfigInfo.getAffiliationMachineUuid());
                // TODO 判断 Set 是否需要重新 put 到缓存中
                // commonPermanentCache.put(monitorPeriodEnum.getDisplay(), vmwareUuids);
            } else {
                // 3.2.2.不存在，则创建监测周期 Set，添加虚拟机 UUID
                ConcurrentHashSet<String> vmwareUuids = new ConcurrentHashSet<>();
                vmwareUuids.add(dynamicConfigInfo.getAffiliationMachineUuid());
                commonPermanentCache.put(monitorPeriodEnum.getDisplay(), vmwareUuids);
            }

            // 4.新增「虚拟机UUID」与「监测周期 display」映射
            commonPermanentCache.put(dynamicConfigInfo.getAffiliationMachineUuid(), monitorPeriodEnum.getDisplay());
        }
    }

    /**
     * 缓存：获取某个监测周期对应的所有虚拟机 UUID
     *
     * @param monitorPeriodEnum 监测周期
     * @return 虚拟机 UUID 列表
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> cacheQueryUuidsByPeriod(MonitorPeriodEnum monitorPeriodEnum) {
        ConcurrentHashSet<String> vmwareUuids = (ConcurrentHashSet<String>) Optional.ofNullable(commonPermanentCache.getIfPresent(monitorPeriodEnum.getDisplay())).orElse(new ConcurrentHashSet<String>());
        return new ArrayList<>(vmwareUuids);
    }
}
