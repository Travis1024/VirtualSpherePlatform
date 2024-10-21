package org.travis.shared.common.constants;

/**
 * @ClassName MonitorConstant
 * @Description MonitorConstant
 * @Author Travis
 * @Data 2024/10
 */
public class MonitorConstant {
    public static final String MONITOR_REDIS_PREFIX = "monitor";
    public static final String SNMP_REDIS_PREFIX = "snmp";
    public static final String IPMI_SEL_PREFIX = "ipmi:sel";
    public static final String IPMI_SENSOR_PREFIX = "ipmi:sensor";
    public static final String SERVICE_PREFIX = "ServiceMonitor";
    public static final String SERVICE_SUM_PREFIX = "ServiceMonitorSum";
    public static final String MONITOR_TRIGGER_REDIS_PREFIX = "trigger*";
    public static final String TRIGGER = "TRIGGER";
    public static final int BUFFER_SIZE = 4096;
    public static final String CPU_STAT = "cpu_stat";
    public static final String MEM_STAT = "mem_stat";
    public static final String DISK_STAT = "disk_stat";
    public static final String NET_STAT = "net_stat";
    public static final String PROCESS_STAT = "process_stat";
    public static final String ALERT_STAT = "alert_stat";


    public static final String INFLUX_TAG_UUID = "uuid";
    public static final String INFLUX_TAG_ADDR = "addr";
    public static final String INFLUX_TAG_INDEX = "index";
    public static final String TIMESTAMP = "timestamp";

    /**
     * ----------------------------------------------------------------------------------
     */
    public static final String NET_CONNECTION_STATS = "connection_stats";
    public static final String CPU_TIME_STATS = "cpu_time_stats";
    public static final String MEM_SWAP_MEMORY_STAT = "swap_memory_stat";
    public static final String MEM_VIRTUAL_MEMORY_STAT = "virtual_memory_stat";
    public static final String MEM_USED_PERCENT = "used_percent";
    public static final String MEM_TOTAL = "total";
    public static final String MEM_AVAILABLE = "available";
    public static final String DISK_PARTITION_WITH_USAGE_STATS = "partition_with_usage_stats";
    public static final String DISK_DEVICE = "device";
    public static final String DISK_MOUNT_POINT = "mountpoint";
    public static final String DISK_USED_PERCENT = "usedPercent";
    public static final String PROCESS_PROCESSES = "processes";

    /**
     * ----------------------------------------------------------------------------------
     */

    public static final String CACHE = "cache";
    public static final String KEY = "key";
    public static final String M = ":";

    public static final String REDIS_KEY_NET = KEY + M + NET_STAT;
    public static final String REDIS_KEY_MEM = KEY + M + MEM_STAT;
    public static final String REDIS_KEY_DISK = KEY + M + DISK_STAT;
    public static final String REDIS_KEY_CPU = KEY + M + CPU_STAT;
    public static final String REDIS_KEY_PROCESS = KEY + M + PROCESS_STAT;

    public static final String CACHE_NET = CACHE + M + NET_STAT;
    public static final String CACHE_MEM = CACHE + M + MEM_STAT;
    public static final String CACHE_DISK = CACHE + M + DISK_STAT;
    public static final String CACHE_CPU = CACHE + M + CPU_STAT;
    public static final String CACHE_PROCESS = CACHE + M + PROCESS_STAT;
    public static final Integer CACHE_LIMIT = 20;
}
