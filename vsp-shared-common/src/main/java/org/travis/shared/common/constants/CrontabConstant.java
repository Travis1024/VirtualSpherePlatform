package org.travis.shared.common.constants;

/**
 * @ClassName CrontabConstant
 * @Description CrontabConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
public class CrontabConstant {

    /**
     * cron 描述模版
     */
    public static final String CRON_DESCRIPTION_TEMPLATE = "执行间隔秒数:{}";

    /**
     * 日志持久化任务固定 ID
     */
    public static final Long LOG_TASK_INDEX_ID = 1L;

    /**
     * 执行周期：5s
     */
    public static final String CRON_5_S = "0/5 * * * * ?";

    /**
     * 执行周期：10s
     */
    public static final String CRON_10_S = "0/10 * * * * ?";

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
}