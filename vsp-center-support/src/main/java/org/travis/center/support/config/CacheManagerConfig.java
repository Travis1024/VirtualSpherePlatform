package org.travis.center.support.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @ClassName CacheManagerConfig
 * @Description Caffeine 缓存配置
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@EnableCaching
@Configuration
public class CacheManagerConfig {

    /**
     * Caffeine 永久缓存
     */
    @Bean("configPermanentCache")
    public Cache<Long, String> getConfigPermanentCache() {
        return Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(400)
                .build();
    }

    @Bean("commonPermanentCache")
    public Cache<String, String> getCommonPermanentCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(200)
                .build();
    }
}
