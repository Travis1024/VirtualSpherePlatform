package org.travis.center.support.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
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
    @Bean("permanentCacheManager")
    public CacheManager getPermanentCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(
                Caffeine.newBuilder()
                        // 初始化缓存空间大小
                        .initialCapacity(100)
                        // 最大缓存条数
                        .maximumSize(200)
        );
        return caffeineCacheManager;
    }
}
