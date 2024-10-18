package org.travis.center.monitor.threads.basic;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.platform.common.constants.MonitorConstant;
import com.hust.platform.common.constants.StatisticConstant;
import com.hust.platform.common.utils.ApplicationContextUtil;
import com.hust.platform.logger.service.LogInfoService;
import com.hust.platform.logger.threads.TaskThreadNumberStatistic;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName TaskProcessDataServiceImpl
 * @Description Process 异步监测任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Slf4j
public class TaskProcessDataServiceImpl implements TaskMonitorDataService {
    private String measurement;
    private String uuid;
    private String jsonStr;
    private InfluxDBClient influxDBClient;
    private RedisTemplate redisTemplate;
    private LogInfoService logInfoService;
    private ThreadPoolExecutor threadPoolExecutor;

    public TaskProcessDataServiceImpl(String measurement, String uuid, String jsonStr) {
        this.measurement = measurement;
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.influxDBClient = ApplicationContextUtil.getBean(InfluxDBClient.class);
        this.redisTemplate = ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
        this.logInfoService = ApplicationContextUtil.getBean(LogInfoService.class);
        this.threadPoolExecutor = ApplicationContextUtil.getBean(ThreadPoolExecutor.class);
    }

    @Override
    public void flattenJsonNode(JsonNode node, String prefix, Map<String, Object> flatMap) {
        // no action
    }

    @Override
    public void saveInfluxDB(Map<String, Object> flatMap, Long timestamp) {
        Point point = Point
                .measurement(measurement)
                .addTag(MonitorConstant.INFLUX_TAG_UUID, uuid)
                .addFields(flatMap)
                .time(new Date(timestamp * 1000L).toInstant(), WritePrecision.MS);
        influxDBClient.getWriteApiBlocking().writePoint(point);
        // influxDBClient.makeWriteApi().writePoint(point);
    }

    @Override
    public void saveKeyToRedis(Map<String, Object> flatMap) {
        redisTemplate.opsForSet().add(MonitorConstant.REDIS_KEY_PROCESS, flatMap.keySet().toArray());
    }

    @Override
    public void saveCacheToRedis(String str) {
        // no action
    }

    @Override
    public void sendWebSocket(String message) {
        // no action
    }

    @Override
    public void run() {
        try {
            // 1、将 json 字符串转为 JsonNode 节点，并提取其中的 process-stat 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            long timestamp = rootNode.get(MonitorConstant.TIMESTAMP).asLong();
            JsonNode processStatNode = rootNode.get(MonitorConstant.PROCESS_STAT);
            Map<String, Object> flatMap = new HashMap<>();
            // 2、获取 processes 节点
            String processesStr = objectMapper.writeValueAsString(processStatNode.get(MonitorConstant.PROCESS_PROCESSES));
            flatMap.put(MonitorConstant.PROCESS_STAT + "." + MonitorConstant.PROCESS_PROCESSES, processesStr);
            // 3、将 key 缓存到 redis 中
            saveKeyToRedis(flatMap);
            // 4、将 Map 数据存入 influxDB
            saveInfluxDB(flatMap, timestamp);

            log.info("[Process 指标解析线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.PROCESS_THREAD, true));
        } catch (Exception e) {
            log.error("[Process-Error]" + e);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.PROCESS_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }
    }
}
