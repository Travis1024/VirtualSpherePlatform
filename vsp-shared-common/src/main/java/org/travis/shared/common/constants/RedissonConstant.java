package org.travis.shared.common.constants;

/**
 * @ClassName RedissonConstant
 * @Description RedissonConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
public class RedissonConstant {
    public static final String WAIT_MONITOR_VMWARE_UUID_LIST = "wait-monitor:vmware";
    public static final String CRONTAB_CACHE_KEY = "crontab:cache";
    public static final String LOG_CACHE_DATA_KEY = "log:cache";
    public static final String DYNAMIC_TABLE_TIMES_PREFIX = "dynamic-table-times:";
    public static final String HEALTHY_PREFIX = "healthy:";
    public static final String HEALTHY_HOST_RECORDS = HEALTHY_PREFIX + "host:";
    public static final String HEALTHY_VMWARE_RECORDS = HEALTHY_PREFIX + "vmware";

    public static final String DYNAMIC_CONFIG_LIST_KEY = "dynamic-config:list";

    public static final String MONITOR_PERIOD_MACHINE_QUEUE_PREFIX = "monitor-period:";

    public static final String VMWARE_MIGRATE_PROGRESS_PREFIX = "vmware:migrate:";
}
