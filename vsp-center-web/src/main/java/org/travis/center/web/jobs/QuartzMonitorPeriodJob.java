package org.travis.center.web.jobs;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Assert;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.travis.center.web.initializer.QuartzJobsDatabaseInitializer;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.enums.MonitorPeriodEnum;

import javax.annotation.Resource;

/**
 * @ClassName QuartzMonitorPeriodJob
 * @Description 监测周期定时刷新任务
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/2
 */
@Slf4j
@Component
public class QuartzMonitorPeriodJob extends QuartzJobBean {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private Cache<String, Object> commonPermanentCache;

    @SuppressWarnings("unchecked")
    @Override
    protected void executeInternal(JobExecutionContext context) {
        Integer second = (Integer) context.getJobDetail().getJobDataMap().get(QuartzJobsDatabaseInitializer.PERIOD_KEY);
        log.info("[定时任务] 监测周期定时刷新任务, 刷新间隔: {}s", second);
        ConcurrentHashSet<String> hashSet = (ConcurrentHashSet<String>) commonPermanentCache.getIfPresent(MonitorPeriodEnum.ofValue(second).getDisplay());
        Assert.isNull(hashSet, () -> {
            log.error("[定时任务] 监测周期定时刷新任务, 刷新间隔: {}s, 缓存中不存在该队列！", second);
            return null;
        });

        RSet<String> rSet = redissonClient.getSet(RedissonConstant.WAIT_MONITOR_VMWARE_UUID_LIST);
        rSet.addAll(hashSet);
        log.info("[定时任务] 监测周期定时刷新任务, 刷新间隔: {}s, 刷新数量: {}, 刷新成功！", second, hashSet.size());
    }
}
