package org.travis.shared.common.constants;

/**
 * @ClassName CrontabConstant
 * @Description CrontabConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
public class ScheduleJobConstant {

    /**
     * 操作日志持久化定时任务 ID
     */
    public static final Long OPERATION_LOG_PERSISTENT_JOB_INDEX_ID = 1L;

    /**
     * 操作日志月份表创建定时任务 ID
     */
    public static final Long LOG_TABLE_CREATE_JOB_INDEX_ID = 2L;

    /**
     * 宿主机虚拟机状态更新定时任务 ID
     */
    public static final Long MACHINE_STATE_UPDATE_JOB_INDEX_ID = 3L;

    /**
     * 周期性监测任务-1s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_1S_INDEX_ID = 4L;

    /**
     * 周期性监测任务-2s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_2S_INDEX_ID = 5L;

    /**
     * 周期性监测任务-3s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_3S_INDEX_ID = 6L;

    /**
     * 周期性监测任务-5s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_5S_INDEX_ID = 7L;

    /**
     * 周期性监测任务-8s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_8S_INDEX_ID = 8L;

    /**
     * 周期性监测任务-10s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_10S_INDEX_ID = 9L;

    /**
     * 周期性监测任务-15s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_15S_INDEX_ID = 10L;

    /**
     * 周期性监测任务-20s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_20S_INDEX_ID = 11L;

    /**
     * 周期性监测任务-30s ID
     */
    public static final Long PERIODIC_MONITOR_JOB_30S_INDEX_ID = 12L;

    /**
     * 物理机、虚拟机数据监测任务 ID
     */
    public static final Long DATA_MONITOR_TASK_JOB_INDEX_ID = 13L;

    /**
     * cron 描述模版
     */
    public static final String CRON_DESCRIPTION_TEMPLATE = "执行间隔秒数:{}";

    /**
     * 执行周期：1s
     */
    public static final String CRON_1_S = "0/1 * * * * ?";
    /**
     * 执行周期：2s
     */
    public static final String CRON_2_S = "0/2 * * * * ?";
    /**
     * 执行周期：3s
     */
    public static final String CRON_3_S = "0/3 * * * * ?";
    /**
     * 执行周期：5s
     */
    public static final String CRON_5_S = "0/5 * * * * ?";
    /**
     * 执行周期：8s
     */
    public static final String CRON_8_S = "0/8 * * * * ?";
    /**
     * 执行周期：10s
     */
    public static final String CRON_10_S = "0/10 * * * * ?";
    /**
     * 执行周期：15s
     */
    public static final String CRON_15_S = "0/15 * * * * ?";
    /**
     * 执行周期：20s
     */
    public static final String CRON_20_S = "0/20 * * * * ?";

    /**
     * 执行周期：30s
     */
    public static final String CRON_30_S = "0/30 * * * * ?";

    /**
     * 执行周期：1 minute
     */
    public static final String CRON_1_M = "0 0/1 * * * ?";

    /**
     * 执行周期：1 hour
     */
    public static final String CRON_1_H = "0 0 0/1 * * ?";

    /**
     * 执行周期：每个月 26—28 号各执行一次
     */
    public static final String CRON_26_27_28_PER_MONTH = "0 0 2 26-28 * ?";
}
