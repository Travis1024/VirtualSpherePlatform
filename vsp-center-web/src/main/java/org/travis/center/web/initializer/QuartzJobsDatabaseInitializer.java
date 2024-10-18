package org.travis.center.web.initializer;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.ScheduleJob;
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.enums.ScheduleGroupEnum;
import org.travis.center.common.mapper.support.ScheduleJobMapper;
import org.travis.center.support.pojo.dto.ScheduleJobCreateDTO;
import org.travis.center.support.service.ScheduleJobService;
import org.travis.center.web.jobs.QuartzLogTableCreateJob;
import org.travis.center.web.jobs.QuartzMachineStateUpdateJob;
import org.travis.center.web.jobs.QuartzMonitorPeriodJob;
import org.travis.center.web.jobs.QuartzOperationLogPersistentJob;
import org.travis.shared.common.constants.ScheduleJobConstant;
import org.travis.shared.common.utils.CrontabUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuartzJobsDatabaseInitializer
 * @Description Quartz 定时任务初始化 + 持久化
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Slf4j
@Component
@Order(2)
public class QuartzJobsDatabaseInitializer implements CommandLineRunner {

    @Resource
    public ScheduleJobService scheduleJobService;
    @Resource
    public ScheduleJobMapper scheduleJobMapper;

    public static final String PERIOD_KEY = "period";
    public static final List<ScheduleJobCreateDTO> SCHEDULE_JOB_INFOS = new ArrayList<>();

    static {
        ScheduleJobCreateDTO operationLogPersistentJob = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.OPERATION_LOG_PERSISTENT_JOB_INDEX_ID)
                .scheduleName("日志持久化定时任务")
                .jobGroup(ScheduleGroupEnum.DATABASE)
                .jobClass(ClassUtil.getClassName(QuartzOperationLogPersistentJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_30_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_30_S)))
                .jobDataMap(null)
                .build();

        ScheduleJobCreateDTO logTableCreateJob = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.LOG_TABLE_CREATE_JOB_INDEX_ID)
                .scheduleName("操作日志月份数据表定时创建任务")
                .jobGroup(ScheduleGroupEnum.DATABASE)
                .jobClass(ClassUtil.getClassName(QuartzLogTableCreateJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_26_27_28_PER_MONTH)
                .cronDescription("执行周期：每月 26-28 号 2:00 各执行一次")
                .jobDataMap(null)
                .build();

        ScheduleJobCreateDTO machineStateUpdateJob = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.MACHINE_STATE_UPDATE_JOB_INDEX_ID)
                .scheduleName("宿主机虚拟机状态定时更新任务")
                .jobGroup(ScheduleGroupEnum.SYSTEM)
                .jobClass(ClassUtil.getClassName(QuartzMachineStateUpdateJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_30_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_30_S)))
                .jobDataMap(null)
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob1s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_1S_INDEX_ID)
                .scheduleName("周期性监测任务-1s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_1_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_1_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 1))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob2s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_2S_INDEX_ID)
                .scheduleName("周期性监测任务-2s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_2_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_2_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 2))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob3s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_3S_INDEX_ID)
                .scheduleName("周期性监测任务-3s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_3_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_3_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 3))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob5s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_5S_INDEX_ID)
                .scheduleName("周期性监测任务-5s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_5_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_5_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 5))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob8s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_8S_INDEX_ID)
                .scheduleName("周期性监测任务-8s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_8_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_8_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 8))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob10s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_10S_INDEX_ID)
                .scheduleName("周期性监测任务-10s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_10_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_10_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 10))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob15s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_15S_INDEX_ID)
                .scheduleName("周期性监测任务-15s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_15_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_15_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 15))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob20s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_20S_INDEX_ID)
                .scheduleName("周期性监测任务-20s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_20_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_20_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 20))
                .build();

        ScheduleJobCreateDTO periodicMonitoringJob30s = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.PERIODIC_MONITOR_JOB_30S_INDEX_ID)
                .scheduleName("周期性监测任务-30s")
                .jobGroup(ScheduleGroupEnum.VMWARE)
                .jobClass(ClassUtil.getClassName(QuartzMonitorPeriodJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_30_S)
                .cronDescription(StrUtil.format(ScheduleJobConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(ScheduleJobConstant.CRON_30_S)))
                .jobDataMap(Collections.singletonMap(PERIOD_KEY, 30))
                .build();

        ScheduleJobCreateDTO dataMonitorTaskJob = ScheduleJobCreateDTO.builder()
                .id(ScheduleJobConstant.DATA_MONITOR_TASK_JOB_INDEX_ID)
                .scheduleName("物理机+虚拟机数据监测任务")
                .jobGroup(ScheduleGroupEnum.SYSTEM)
                .jobClass(ClassUtil.getClassName(QuartzMonitorTaskJob.class, false))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .cronExpression(ScheduleJobConstant.CRON_30_S)

        SCHEDULE_JOB_INFOS.add(operationLogPersistentJob);
        SCHEDULE_JOB_INFOS.add(logTableCreateJob);
        SCHEDULE_JOB_INFOS.add(machineStateUpdateJob);

        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob1s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob2s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob3s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob5s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob8s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob10s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob15s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob20s);
        SCHEDULE_JOB_INFOS.add(periodicMonitoringJob30s);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("[2] Initializing CrontabInfo Records");
        Set<Long> initIds = SCHEDULE_JOB_INFOS.stream().map(ScheduleJobCreateDTO::getId).collect(Collectors.toSet());

        if (!initIds.isEmpty()) {
            Set<Long> existIds = scheduleJobMapper.selectBatchIds(initIds).stream().map(ScheduleJob::getId).collect(Collectors.toSet());
            initIds.removeAll(existIds);
            log.info(JSONUtil.toJsonStr(initIds));
            SCHEDULE_JOB_INFOS.forEach(scheduleJobCreateDTO -> {
                if (initIds.contains(scheduleJobCreateDTO.getId())) {
                    try {
                        ScheduleJob scheduleJob = scheduleJobService.createScheduleJob(scheduleJobCreateDTO);
                        log.info(JSONUtil.toJsonPrettyStr(scheduleJob));
                    } catch (SchedulerException scheduledException) {
                        log.error(StrUtil.format("{} Handle Failed.", scheduleJobCreateDTO.getId()), scheduledException);
                    }
                }
            });
        }

        log.info("[2] Initializing CrontabInfo Records Completed.");
    }
}
