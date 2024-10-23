package org.travis.center.web.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @ClassName QuartzDataMonitorRegulateTaskJob
 * @Description QuartzDataMonitorRegulateTaskJob
 * @Author Travis
 * @Data 2024/10
 */
@Slf4j
@Component
public class QuartzDataMonitorRegulateTaskJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

    }
}
