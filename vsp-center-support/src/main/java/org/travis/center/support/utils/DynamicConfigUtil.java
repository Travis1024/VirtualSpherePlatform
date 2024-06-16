package org.travis.center.support.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.shared.common.exceptions.NotFoundException;

import javax.annotation.Resource;

/**
 * @ClassName DynamicConfigUtil
 * @Description CaffeineUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Slf4j
@Component
public class DynamicConfigUtil {

    @Resource
    public Cache<Long, String> configPermanentCache;
    @Resource
    public DynamicConfigInfoMapper dynamicConfigInfoMapper;

    public String getConfigValue(Long configId) {
        synchronized (getConfigLock(configId)) {
            String cacheValue = configPermanentCache.getIfPresent(configId);
            if (StrUtil.isBlank(cacheValue)) {
                DynamicConfigInfo configInfo = dynamicConfigInfoMapper.selectById(configId);
                Assert.isTrue(ObjectUtil.isNotNull(configInfo), () -> new NotFoundException("未找到相关配置!"));
                cacheValue = configInfo.getConfigValue();
                configPermanentCache.put(configId, cacheValue);
            }
            return cacheValue;
        }
    }

    @Transactional
    public void updateConfigValue(Long configId, String configValue) {
        synchronized (getConfigLock(configId)) {
            dynamicConfigInfoMapper.update(
                    Wrappers.<DynamicConfigInfo>lambdaUpdate()
                            .set(DynamicConfigInfo::getConfigValue, configValue)
                            .eq(DynamicConfigInfo::getId, configId)
            );
            configPermanentCache.invalidate(configId);
        }
    }


    public static String getConfigLock(Long configId) {
        return "configLock:" + configId;
    }

}
