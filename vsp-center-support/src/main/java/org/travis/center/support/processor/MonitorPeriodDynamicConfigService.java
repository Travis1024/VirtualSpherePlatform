package org.travis.center.support.processor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigTypeEnum;
import org.travis.center.support.utils.DynamicConfigLockUtil;
import org.travis.shared.common.constants.RedissonConstant;
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

    @Transactional
    @Override
    public void updateConfigValue(String configValue) {
        // 0.临时保存旧配置参数 + 更新配置值 + 校验新配置参数
        String oldConfigValue = dynamicConfigInfo.getConfigValue();
        dynamicConfigInfo.setConfigValue(configValue);
        Optional.ofNullable(MonitorPeriodEnum.ofValue(Integer.valueOf(configValue))).orElseThrow(() -> new RuntimeException("配置参数不合法"));

        // 1.更新持久化配置
        dynamicConfigInfoMapper.update(
                Wrappers.<DynamicConfigInfo>lambdaUpdate()
                        .set(DynamicConfigInfo::getConfigValue, configValue)
                        .eq(DynamicConfigInfo::getId, dynamicConfigInfo.getId())
        );
        // 2.删除缓存
        RMap<Long, DynamicConfigInfo> rMap = redissonClient.getMap(RedissonConstant.DYNAMIC_CONFIG_LIST_KEY);
        rMap.remove(dynamicConfigInfo.getId());

        // 3.从旧 redis 缓存队列中删除配置
        MonitorPeriodEnum oldMonitorPeriodEnum = MonitorPeriodEnum.ofValue(Integer.valueOf(oldConfigValue));
        RSet<String> rSet = redissonClient.getSet(RedissonConstant.MONITOR_PERIOD_MACHINE_QUEUE_PREFIX + oldMonitorPeriodEnum.getDisplay());
        if (null != rSet) {
            rSet.remove(dynamicConfigInfo.getAffiliationMachineUuid());
        }

        // 4.缓存到 redis 中
        addMonitorPeriodCache(dynamicConfigInfo);
    }

    @Transactional
    @Override
    public void insertConfigValue() {
        synchronized (DynamicConfigLockUtil.getVmwarePeriodMapLock(String.valueOf(dynamicConfigInfo.getAffiliationMachineId()))) {
            // 1.持久化到数据库中
            dynamicConfigInfoMapper.insert(dynamicConfigInfo);
            // 2.缓存到 redis 中
            addMonitorPeriodCache(dynamicConfigInfo);
        }
    }

    @Transactional
    public void addMonitorPeriodCache(DynamicConfigInfo dynamicConfigInfo) {
        // 1.缓存到 redis
        RMap<Long, DynamicConfigInfo> rMap = redissonClient.getMap(RedissonConstant.DYNAMIC_CONFIG_LIST_KEY);
        rMap.put(dynamicConfigInfo.getId(), dynamicConfigInfo);

        // 2.向 redis 缓存队列中添加配置 <监测周期-display, 「主机UUID」Set>
        // 2.1.解析当前动态配置-监测周期
        MonitorPeriodEnum monitorPeriodEnum = MonitorPeriodEnum.ofValue(Integer.valueOf(dynamicConfigInfo.getConfigValue()));
        // 2.2.向 redis 监测周期队列中添加 「主机UUID」
        RSet<String> rSet = redissonClient.getSet(RedissonConstant.MONITOR_PERIOD_MACHINE_QUEUE_PREFIX + monitorPeriodEnum.getDisplay());
        rSet.add(dynamicConfigInfo.getAffiliationMachineUuid());

        // 3.新增「主机UUID」与「监测周期 display」映射
        // commonPermanentCache.put(dynamicConfigInfo.getAffiliationMachineUuid(), monitorPeriodEnum.getDisplay());
    }

    /**
     * 缓存：获取某个监测周期对应的所有「主机 UUID」
     *
     * @param monitorPeriodEnum 监测周期
     * @return 「主机 UUID」列表
     */
    @Override
    public List<String> cacheQueryUuidsByPeriod(MonitorPeriodEnum monitorPeriodEnum) {
        RSet<String> rSet = redissonClient.getSet(RedissonConstant.MONITOR_PERIOD_MACHINE_QUEUE_PREFIX + monitorPeriodEnum.getDisplay());
        return rSet == null ? new ArrayList<>() : new ArrayList<>(rSet);
    }
}
