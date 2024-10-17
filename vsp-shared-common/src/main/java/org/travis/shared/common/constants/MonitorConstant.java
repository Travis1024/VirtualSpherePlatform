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
}
