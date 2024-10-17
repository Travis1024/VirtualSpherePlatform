package org.travis.center.common.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @ClassName RedisConfiguration
 * @Description Redis配置类
 * @Author travis-wei
 * @Version v1.0
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHostIp;
    @Value("${spring.redis.port}")
    private Integer redisHostPort;
    @Value("${spring.redis.password}")
    private String redisHostPasswd;

    @Bean(name = "gzipRedisTemplate")
    public RedisTemplate<String, Object> gzipRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // Gip 序列化器
        JacksonGzipSerializer jacksonGzipSerializer = new JacksonGzipSerializer();
        // String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 采用String的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // value 序列化方式采用 gzip
        redisTemplate.setValueSerializer(jacksonGzipSerializer);
        // hash value的序列化也用 gzip
        redisTemplate.setHashValueSerializer(jacksonGzipSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
