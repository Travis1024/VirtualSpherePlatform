package org.travis.center.web.initializer;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName DynamicConfigCacheInitializer
 * @Description DynamicConfigCacheInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Slf4j
@Component
@Order(5)
public class DynamicConfigCacheInitializer implements CommandLineRunner {

    @Resource
    public DynamicConfigInfoMapper dynamicConfigInfoMapper;
    @Resource
    public Cache<Long, String> configPermanentCache;

    @Override
    public void run(String... args) {
        log.info("[5] Initializing Dynamic Config Cache");
        // 查询所有动态配置 ID 列表
        List<Long> configIds = DynamicConfigDatabaseInitializer.DYNAMIC_CONFIG_INFOS.stream().map(DynamicConfigInfo::getId).collect(Collectors.toList());
        // 根据 ID 列表查询动态配置信息
        List<DynamicConfigInfo> dynamicConfigInfos = dynamicConfigInfoMapper.selectBatchIds(configIds);
        // 缓存到 Caffeine 本地缓存
        dynamicConfigInfos.forEach(dynamicConfigInfo -> configPermanentCache.put(dynamicConfigInfo.getId(), dynamicConfigInfo.getConfigValue()));
        log.info("[5] Initializing Dynamic Config Cache Completed.");
    }
}
