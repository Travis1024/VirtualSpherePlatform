package org.travis.center.web.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
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

    // TODO [非必要] 此类需要改造：兼容不同的短周期定时任务的执行

    @Override
    protected void executeInternal(JobExecutionContext context) {
        Integer second = (Integer) context.getJobDetail().getJobDataMap().get(QuartzJobsDatabaseInitializer.PERIOD_KEY);
        RSet<String> rSet = redissonClient.getSet(RedissonConstant.MONITOR_PERIOD_MACHINE_QUEUE_PREFIX + MonitorPeriodEnum.ofValue(second).getDisplay());
        if (rSet.isEmpty()) {
            log.debug("[定时任务] 监测周期定时刷新任务, 刷新间隔: {}s, 无刷新任务!", second);
            return;
        }

        // Go端定时任务消费 WAIT_MONITOR_VMWARE_UUID_LIST 中的数据，完成监测数据推送
        RSet<String> waitRedisSet = redissonClient.getSet(RedissonConstant.WAIT_MONITOR_VMWARE_UUID_LIST);
        waitRedisSet.addAll(rSet.readAll());
        log.info("[定时任务] 监测周期定时刷新任务, 刷新间隔: {}s, 刷新数量: {}, 刷新成功！", second, rSet.size());
    }
}
