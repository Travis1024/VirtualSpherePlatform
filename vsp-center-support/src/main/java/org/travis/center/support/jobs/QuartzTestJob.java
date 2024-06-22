package org.travis.center.support.jobs;

import org.quartz.JobExecutionContext;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName QuartzTestJob
 * @Description QuartzTestJob
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Component
public class QuartzTestJob extends QuartzJobBean {

    @Resource
    private RedissonClient redissonClient;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        System.out.println("QuartzTestJob execute");
        RBucket<String> rBucket = redissonClient.getBucket("quartz");
        rBucket.setIfAbsent("quartz");
    }

}
