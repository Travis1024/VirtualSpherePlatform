package org.travis.center.web.initializer;

import cn.hutool.core.date.DateUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.shared.common.constants.SystemConstant;

import javax.annotation.Resource;

/**
 * @ClassName InitProgramStartTime
 * @Description 初始化程序启动时间（本地缓存）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class InitProgramStartTime implements CommandLineRunner {

    @Resource
    public Cache<String, Object> commonPermanentCache;

    @Override
    public void run(String... args) {
        long timeMillis = System.currentTimeMillis();
        log.info("[Eventual] Program started time:{} | {}", timeMillis, DateUtil.date(timeMillis));
        commonPermanentCache.put(SystemConstant.PROGRAM_START_TIME_KEY, timeMillis);
        log.info("[Eventual] Program started time cache finished!");
    }
}
