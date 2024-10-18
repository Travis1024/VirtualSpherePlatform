package org.travis.center.web.jobs;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.travis.center.common.utils.MonitorRedisUtil;
import org.travis.center.common.utils.MonitorThreadPoolConfig;
import org.travis.center.monitor.threads.addition.*;
import org.travis.center.monitor.threads.basic.*;
import org.travis.shared.common.constants.MonitorConstant;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName SingleMonitorTask
 * @Description 监测数据定时任务
 * @Author travis-wei
 * @Version v1.0
 */
@Slf4j
@Component
public class QuartzDataMonitorTaskJob extends QuartzJobBean {

    @Resource
    private MonitorRedisUtil monitorRedisUtil;

    @Resource(name = "gzipRedisTemplate")
    private RedisTemplate<String, Object> gzipRedisTemplate;

    @Value("${influx.measurement}")
    private String measurement;

    @Value("${alarmIntervalTime}")
    private Long alarmIntervalTime;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            String simpleUUID = IdUtil.fastSimpleUUID();
            log.info("[QuartzDataMonitorTaskJob] UUID:{}, ThreadName:{}",simpleUUID, Thread.currentThread().getName());

            // 处理「指标监控数据」（数据解析 + 数据存储）
            processIndicatorMonitorData();
            // 处理「报警触发事件」
            processAlarmTriggerEvent();
            // SNMP 监测线程（数据解析 + 数据存储）
            processSnmpMonitorData();
            // IPMI:SEL 监测线程（数据解析 + 数据存储）
            processIpmiSelMonitorData();
            // IPMI:SENSOR 监测线程（数据解析 + 数据存储）
            processIpmiSensorMonitorData();

        } catch (Exception e) {
            log.error(e.toString());
            log.error("当前周期内数据监控任务异常退出，请检查报错信息！\n {}", e.getMessage());
        }
    }

    /**
     * 处理「IPMI:SENSOR 监测数据」
     */
    private void processIpmiSensorMonitorData() {
        Set<String> ipmiSensorKeys = monitorRedisUtil.scan(MonitorConstant.IPMI_SENSOR_PREFIX);
        // key-eg: ipmi:sensor:6e8c2828-5460-49a2-99a9-6ed809ae4d1d
        for (String ipmiSensorKey : ipmiSensorKeys) {
            String jsonStr = (String) gzipRedisTemplate.opsForList().rightPop(ipmiSensorKey);

            if (StrUtil.isEmpty(jsonStr)) {
                continue;
            }
            String uuid = ipmiSensorKey.substring(12);

            // addition:IPMI:SENSOR数据解析线程
            CompletableFuture.runAsync(new ThreadIpmiSensorMonitor(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
        }
    }

    /**
     * 处理「IPMI:SEL 监测数据」
     */
    private void processIpmiSelMonitorData() {
        Set<String> ipmiSelKeys = monitorRedisUtil.scan(MonitorConstant.IPMI_SEL_PREFIX);
        // key-eg: ipmi:sel:6e8c2828-5460-49a2-99a9-6ed809ae4d1d
        for (String ipmiSelKey : ipmiSelKeys) {
            String jsonStr = (String) gzipRedisTemplate.opsForList().rightPop(ipmiSelKey);
            if (StrUtil.isEmpty(jsonStr)) {
                continue;
            }
            String uuid = ipmiSelKey.substring(9);

            // addition:IPMI:SEL数据解析线程
            CompletableFuture.runAsync(new ThreadIpmiLogMonitor(uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
        }
    }

    /**
     * 处理「SNMP监控数据」
     */
    private void processSnmpMonitorData() {
        Set<String> snmpKeys = monitorRedisUtil.scan(MonitorConstant.SNMP_REDIS_PREFIX);
        // key-eg: snmp:6e8c2828-5460-49a2-99a9-6ed809ae4d1d
        for (String snmpKey : snmpKeys) {
            String jsonStr = (String) gzipRedisTemplate.opsForList().rightPop(snmpKey);
            if (StrUtil.isEmpty(jsonStr)) {
                continue;
            }
            String uuid = snmpKey.substring(5);

            // addition:SNMP数据解析线程
            CompletableFuture.runAsync(new ThreadSnmpMonitor(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
        }
    }

    /**
     * 处理「报警触发事件」
     */
    private void processAlarmTriggerEvent() {
        Set<String> triggerKeys = monitorRedisUtil.scan(MonitorConstant.MONITOR_TRIGGER_REDIS_PREFIX);
        for (String triggerKey : triggerKeys) {
            String jsonStr = (String) gzipRedisTemplate.opsForList().rightPop(triggerKey);
            if (StrUtil.isEmpty(jsonStr)) {
                continue;
            }
            String uuid = triggerKey.substring(8);

            // addition:报警触发事件线程
            CompletableFuture.runAsync(new ThreadTriggerMonitor(uuid, jsonStr, alarmIntervalTime), MonitorThreadPoolConfig.monitorProcessExecutor);
        }
    }

    /**
     * 处理「指标监控数据」
     */
    private void processIndicatorMonitorData() {
        Set<String> keys = monitorRedisUtil.scan(MonitorConstant.MONITOR_REDIS_PREFIX);
        // key-eg: monitor:6e8c2828-5460-49a2-99a9-6ed809ae4d1d
        for (String key : keys) {
            String jsonStr = (String) gzipRedisTemplate.opsForList().rightPop(key);

            if (StrUtil.isEmpty(jsonStr)) {
                continue;
            }
            String uuid = key.substring(8);

            /**
             * 「异步执行」 数据解析 + 数据存储 + 数据报警
             */
            // basic:网络模块数据解析
            CompletableFuture.runAsync(new TaskNetDataServiceImpl(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
            // basic:CPU模块数据解析
            CompletableFuture.runAsync(new TaskCpuDataServiceImpl(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
            // basic:内存模块数据解析
            CompletableFuture.runAsync(new TaskMemDataServiceImpl(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
            // basic:磁盘模块数据解析
            CompletableFuture.runAsync(new TaskDiskDataServiceImpl(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
            // basic:进程模块数据解析
            CompletableFuture.runAsync(new TaskProcessDataServiceImpl(measurement, uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);

            // addition:报警监控线程
            CompletableFuture.runAsync(new ThreadAlarmMonitor(uuid, jsonStr, alarmIntervalTime), MonitorThreadPoolConfig.monitorProcessExecutor);
            // addition:服务监控线程
            CompletableFuture.runAsync(new ThreadServiceMonitor(uuid, jsonStr), MonitorThreadPoolConfig.monitorProcessExecutor);
        }
    }
}
