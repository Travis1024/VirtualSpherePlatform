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
@Deprecated
@EnableCaching
@Configuration
public class CacheManagerConfig {

    @Bean("commonPermanentCache")
    public Cache<String, Object> getCommonPermanentCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(200)
                .build();
    }
}
