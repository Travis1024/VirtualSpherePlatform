package org.travis.center.support.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.support.pojo.dto.QuartzCreateParamDTO;
import org.travis.center.support.pojo.dto.QuartzJobKeyDTO;
import org.travis.center.support.pojo.dto.QuartzUpdateParamDTO;
import org.travis.center.support.pojo.vo.QuartzJobDetailVO;
import org.travis.center.support.pojo.vo.QuartzTriggerDetailVO;
import org.travis.center.support.service.QuartzService;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.NotFoundException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName QuartzServiceImpl
 * @Description QuartzServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
@Service
public class QuartzServiceImpl implements QuartzService {
    @Resource
    private Scheduler scheduler;
    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    @Transactional
    @SuppressWarnings("unchecked")
    @Override
    public void addJob(QuartzCreateParamDTO param) throws SchedulerException {
        String jobClazz = param.getJobClazz();
        String jobName = param.getJobName();
        String jobGroup = param.getJobGroup();
        String triggerCrontab = param.getTriggerCrontab();
        String jobDescription = param.getJobDescription();

        // 1. 校验任务标识是否存在
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        boolean existed = checkJobExist(jobKey);
        Assert.isFalse(existed, () -> new BadRequestException("定时任务标识已存在!"));

        // 2.获取 class 对象
        Class<? extends QuartzJobBean> jobClass;
        // 使用 ClassUtil.loadClass 获取 Class 对象
        Class<?> clazz = ClassUtil.loadClass(jobClazz);
        // 检查加载的类是否是 QuartzJobBean 的子类
        Assert.isTrue(QuartzJobBean.class.isAssignableFrom(clazz), () -> new BadRequestException("任务类必须是「QuartzJobBean」的子类"));
        // 获取 Class 对象
        jobClass = (Class<? extends QuartzJobBean>) clazz;

        // 3.设置任务数据
        JobDataMap jobDataMap = new JobDataMap();
        if (param.getJobDataMap() != null) {
            jobDataMap.putAll(param.getJobDataMap());
        }

        // 4.获取 Scheduler 定时任务对象
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        // 4.1.创建 JobDetail 对象
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .withDescription(jobDescription)
                .usingJobData(jobDataMap)
                .build();

        // 4.2.构建 Cron 触发器实例
        CronTriggerImpl cronTrigger = (CronTriggerImpl) CronScheduleBuilder
                .cronSchedule(triggerCrontab)
                .withMisfireHandlingInstructionDoNothing()
                .build();
        cronTrigger.setKey(TriggerKey.triggerKey(jobName, jobGroup));

        // 4.3.安排定时任务
        scheduler.scheduleJob(jobDetail, cronTrigger);

        // 5.启动定时任务
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    @Transactional
    @Override
    public void updateJob(QuartzUpdateParamDTO param) throws SchedulerException {
        String jobName = param.getJobName();
        String jobGroup = param.getJobGroup();
        String triggerCrontab = param.getTriggerCrontab();

        // 1. 校验任务标识是否存在
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        boolean existed = checkJobExist(jobKey);
        Assert.isTrue(existed, () -> new NotFoundException("定时任务标识定位失败!"));

        // 2.new/old use the same key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        // 3.构建新 Cron 触发器实例
        CronTriggerImpl cronTrigger = (CronTriggerImpl) CronScheduleBuilder
                .cronSchedule(triggerCrontab)
                .withMisfireHandlingInstructionDoNothing()
                .build();
        cronTrigger.setKey(TriggerKey.triggerKey(jobName, jobGroup));

        // 4.更新定时任务
        scheduler.rescheduleJob(triggerKey, cronTrigger);
    }

    @Override
    public void pauseJob(QuartzJobKeyDTO param) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(param.getJobName(), param.getJobGroup());
        boolean existed = checkJobExist(jobKey);
        Assert.isTrue(existed, () -> new NotFoundException("定时任务标识定位失败!"));
        scheduler.pauseJob(jobKey);
    }

    @Override
    public void resumeJob(QuartzJobKeyDTO param) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(param.getJobName(), param.getJobGroup());
        boolean existed = checkJobExist(jobKey);
        Assert.isTrue(existed, () -> new NotFoundException("定时任务标识定位失败!"));
        scheduler.resumeJob(jobKey);
    }

    @Override
    public void deleteJob(QuartzJobKeyDTO param) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(param.getJobName(), param.getJobGroup());
        boolean existed = checkJobExist(jobKey);
        Assert.isTrue(existed, () -> new NotFoundException("定时任务标识定位失败!"));

        // 先暂停再删除
        scheduler.pauseJob(jobKey);
        scheduler.deleteJob(jobKey);
    }

    @Override
    public List<QuartzJobDetailVO> jobList() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        List<QuartzJobDetailVO> jobDetailList = new ArrayList<>();
        for (JobKey jobKey : scheduler.getJobKeys(matcher)) {
            QuartzJobDetailVO jobDetailVO = getJobDetailByJobKey(jobKey);
            jobDetailList.add(jobDetailVO);
        }
        return jobDetailList;
    }

    @Override
    public QuartzJobDetailVO jobDetail(QuartzJobKeyDTO param) throws SchedulerException {
        String jobName = param.getJobName();
        String jobGroup = param.getJobGroup();
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        return getJobDetailByJobKey(jobKey);
    }

    /**
     * 检查定时任务是否存在
     * @param jobKey jobKey
     * @return true:存在，false:不存在
     */
    private boolean checkJobExist(JobKey jobKey) throws SchedulerException {
        return scheduler.checkExists(jobKey);
    }

    /**
     * 查询 Job 信息及其触发器信息
     * @param jobKey jobKey
     * @return {@link QuartzJobDetailVO}
     */
    @SuppressWarnings("unchecked")
    private QuartzJobDetailVO getJobDetailByJobKey(JobKey jobKey) throws SchedulerException {
        // 1.查询任务信息
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        // 2.拼接任务信息
        QuartzJobDetailVO jobDetailVO = new QuartzJobDetailVO();
        jobDetailVO.setJobClazz(jobDetail.getJobClass().toString());
        jobDetailVO.setJobName(jobKey.getName());
        jobDetailVO.setJobGroup(jobKey.getGroup());
        jobDetailVO.setJobDescription(jobDetail.getDescription());
        jobDetailVO.setJobDataMap(jobDetail.getJobDataMap());

        // 3.查询触发器信息
        List<Trigger> triggerList = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
        List<QuartzTriggerDetailVO> triggerDetailList = new ArrayList<>();

        for (Trigger trigger : triggerList) {
            QuartzTriggerDetailVO triggerDetailVO = new QuartzTriggerDetailVO();

            if (trigger instanceof CronTriggerImpl) {
                // 获取触发器：Cron 表达式
                CronTriggerImpl cronTriggerImpl = (CronTriggerImpl) trigger;
                String cronExpression = cronTriggerImpl.getCronExpression();
                triggerDetailVO.setTriggerCrontab(cronExpression);

                // 获取触发器：最近 10 次的触发时间
                List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 10);
                triggerDetailVO.setRecentFireTimeList(dates);
            }

            // 获取触发器：状态
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            triggerDetailVO.setTriggerState(triggerState.toString());

            triggerDetailList.add(triggerDetailVO);
        }
        jobDetailVO.setTriggerDetailList(triggerDetailList);

        return jobDetailVO;
    }
}
