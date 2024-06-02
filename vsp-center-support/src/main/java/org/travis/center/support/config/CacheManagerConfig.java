package org.travis.center.support.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Bean("permanentCache")
    public Cache<Long, String> getPermanentCache() {
        return Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(400)
                .build();
    }
}
