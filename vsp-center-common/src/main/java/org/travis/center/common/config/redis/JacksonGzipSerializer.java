package org.travis.center.common.config.redis;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.travis.center.common.utils.GzipUtil;

/**
 * @ClassName JacksonGzipSerializer
 * @Description 自定义 Gzip 序列化器
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/16
 */
public class JacksonGzipSerializer extends GenericJackson2JsonRedisSerializer {

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        // 实际应用时 source 为字符串，首先将字符串转为 byte[]
        byte[] data = super.serialize(source);
        try {
            return GzipUtil.serialize(data);
        } catch (Exception e) {
            throw new SerializationException("自定义 Redis-Gzip 序列化失败！");
        }
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        return GzipUtil.deserialize(source);
    }
}
