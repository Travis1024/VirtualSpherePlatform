package org.travis.center.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.MultiKeyCommands;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName RedisUtil
 * @Description Redis 工具类
 * @Author travis-wei
 * @Version v1.0
 */
@Slf4j
@Component
public class MonitorRedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public Set<String> scan(String keyPrefix) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();

            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
            MultiKeyCommands multiKeyCommands = (MultiKeyCommands) commands;

            ScanParams scanParams = new ScanParams();
            scanParams.match(keyPrefix + "*");
            scanParams.count(1000);
            ScanResult<String> scan = multiKeyCommands.scan("0", scanParams);
            while (null != scan.getCursor()) {
                keys.addAll(scan.getResult());
                if (!StringUtils.equals("0", scan.getCursor())) {
                    scan = multiKeyCommands.scan(scan.getCursor(), scanParams);
                } else {
                    break;
                }
            }
            return keys;
        });
    }

    public Integer scanCount(String keyPrefix) {
        return scan(keyPrefix).size();
    }
}
