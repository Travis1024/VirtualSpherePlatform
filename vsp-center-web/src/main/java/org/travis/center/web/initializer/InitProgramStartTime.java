package org.travis.center.web.initializer;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.shared.common.constants.SystemConstant;

import javax.annotation.Resource;

/**
 * @ClassName InitProgramStartTime
 * @Description Redis缓存初始化程序启动时间「LOWEST_PRECEDENCE」
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Slf4j
@Component
@Order
public class InitProgramStartTime implements CommandLineRunner {

    @Resource
    public RedissonClient redissonClient;

    @Override
    public void run(String... args) {
        long timeMillis = System.currentTimeMillis();
        log.info("[Eventual] Program started time:{} | {}", timeMillis, DateUtil.date(timeMillis));
        RBucket<Long> rBucket = redissonClient.getBucket(SystemConstant.PROGRAM_START_TIME_KEY);
        rBucket.set(timeMillis);
        log.info("[Eventual] Program started time cache finished!");
    }
}
