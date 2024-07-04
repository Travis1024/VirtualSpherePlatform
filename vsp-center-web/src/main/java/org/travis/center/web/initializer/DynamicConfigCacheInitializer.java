package org.travis.center.web.initializer;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.shared.common.constants.RedissonConstant;

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
@Order(4)
public class DynamicConfigCacheInitializer implements CommandLineRunner {

    @Resource
    public DynamicConfigInfoMapper dynamicConfigInfoMapper;
    @Resource
    public RedissonClient redissonClient;

    @Override
    public void run(String... args) {
        log.info("[4] Initializing Dynamic Config Cache");
        // 1.1.查询所有动态配置 ID 列表
        List<Long> configIds = DynamicConfigDatabaseInitializer.DYNAMIC_CONFIG_INFOS.stream().map(DynamicConfigInfo::getId).collect(Collectors.toList());
        if (!configIds.isEmpty()) {
            // 1.2.根据 ID 列表查询动态配置信息
            List<DynamicConfigInfo> dynamicConfigInfos = dynamicConfigInfoMapper.selectBatchIds(configIds);
            // 1.3.缓存到 redis
            RMap<Long, DynamicConfigInfo> rMap = redissonClient.getMap(RedissonConstant.DYNAMIC_CONFIG_LIST_KEY);
            dynamicConfigInfos.forEach(dynamicConfigInfo -> rMap.put(dynamicConfigInfo.getId(), dynamicConfigInfo));
        }
        log.info("[4] Initializing Dynamic Config Cache Completed.");
    }
}
