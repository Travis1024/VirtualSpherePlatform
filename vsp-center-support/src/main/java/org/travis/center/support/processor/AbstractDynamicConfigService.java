package org.travis.center.support.processor;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigTypeEnum;
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.support.utils.DynamicConfigLockUtil;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.enums.MonitorPeriodEnum;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.exceptions.NotFoundException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName AbstractDynamicConfigHandler
 * @Description 动态配置抽象类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/2
 */
@Slf4j
public abstract class AbstractDynamicConfigService {

    @Resource
    public DynamicConfigInfoMapper dynamicConfigInfoMapper;
    @Resource
    public RedissonClient redissonClient;

    protected DynamicConfigInfo dynamicConfigInfo;
    protected DynamicConfigTypeEnum dynamicConfigTypeEnum;

    @PostConstruct
    public void init() {
        AbstractDynamicConfigHolder.addDynamicConfigHandler(dynamicConfigTypeEnum, this);
    }

    public void executeInsertValue(DynamicConfigInfo dynamicConfigInfo) {
        this.dynamicConfigInfo = dynamicConfigInfo;
        log.info("执行插入配置：{} - {}", dynamicConfigTypeEnum, JSONUtil.toJsonPrettyStr(dynamicConfigInfo));
        insertConfigValue();
        log.info("执行插入配置成功：{} - {}", dynamicConfigTypeEnum, dynamicConfigInfo.getId());
    }

    @Transactional
    public void executeUpdateValue(DynamicConfigInfo dynamicConfigInfo, String newConfigValue) {
        this.dynamicConfigInfo = dynamicConfigInfo;
        log.info("执行更新配置：{} - {}", dynamicConfigTypeEnum, JSONUtil.toJsonPrettyStr(dynamicConfigInfo));

        // 1. 判断当前动态配置是否允许修改
        Assert.isTrue(isAllowUpdate(), () -> new ForbiddenException("当前动态配置不允许修改!"));

        // 2.获取配置锁
        synchronized (DynamicConfigLockUtil.getConfigLock(dynamicConfigInfo.getId())) {
            // 3.执行更新操作
            updateConfigValue(newConfigValue);
        }

        log.info("执行更新配置成功：{} - {}", dynamicConfigTypeEnum, dynamicConfigInfo.getId());
    }

    public void executeDeleteValue(Long configId) {
        log.info("执行删除配置：{} - {}", dynamicConfigTypeEnum, configId);
        // 1.获取配置锁
        synchronized (DynamicConfigLockUtil.getConfigLock(configId)) {
            // 2.执行删除操作
            deleteConfigValue(configId);
        }
        log.info("执行删除配置成功：{} - {}", dynamicConfigTypeEnum, configId);
    }

    public String executeQueryValue(Long configId) {
        log.info("执行查询配置：{} - {}", dynamicConfigTypeEnum, configId);
        // 1.获取配置锁
        synchronized (DynamicConfigLockUtil.getConfigLock(configId)) {

            // 2.查询缓存是否命中
            String cacheValue;
            RMap<Long, DynamicConfigInfo> rMap = redissonClient.getMap(RedissonConstant.DYNAMIC_CONFIG_LIST_KEY);
            DynamicConfigInfo dynamicConfigInfo = rMap.get(configId);

            if (null == dynamicConfigInfo) {
                // 3.1.缓存未命中：查询数据库
                DynamicConfigInfo configInfo = Optional.ofNullable(dynamicConfigInfoMapper.selectById(configId)).orElseThrow(() -> new NotFoundException("未找到相关配置!"));
                cacheValue = configInfo.getConfigValue();
                // 3.2.缓存未命中 + 查询成功：缓存数据
                rMap.put(configId, configInfo);
            } else {
                // 缓存命中
                cacheValue = dynamicConfigInfo.getConfigValue();
            }

            // 4.返回数据
            log.info("执行查询配置成功：{} - {}", configId, cacheValue);
            return cacheValue;
        }
    }

    /**
     * 判断当前动态配置是否允许修改
     *
     * @return boolean
     */
    public boolean isAllowUpdate() {
        return dynamicConfigInfo.getIsFixed().equals(IsFixedEnum.ALLOW_UPDATE);
    }

    /**
     * {@link MonitorPeriodDynamicConfigService}
     */
    public List<String> cacheQueryUuidsByPeriod(MonitorPeriodEnum monitorPeriodEnum) {
        return null;
    }

    public static AbstractDynamicConfigService getMatchedService(DynamicConfigTypeEnum dynamicConfigTypeEnum) {
        if (DynamicConfigTypeEnum.MONITOR_PERIOD.equals(dynamicConfigTypeEnum)) {
            return AbstractDynamicConfigHolder.getDynamicConfigHandler(DynamicConfigTypeEnum.MONITOR_PERIOD);
        } else if (DynamicConfigTypeEnum.isUniversal(dynamicConfigTypeEnum)) {
            return AbstractDynamicConfigHolder.getDynamicConfigHandler(DynamicConfigTypeEnum.UNIVERSAL);
        } else {
            return null;
        }
    }

    public abstract void deleteConfigValue(Long configId);

    public abstract void updateConfigValue(String configValue);

    public abstract void insertConfigValue();
}
