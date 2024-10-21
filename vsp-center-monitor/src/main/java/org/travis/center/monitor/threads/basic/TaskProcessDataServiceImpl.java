package org.travis.center.monitor.threads.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.shared.common.constants.MonitorConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TaskProcessDataServiceImpl
 * @Description Process 异步监测任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Slf4j
public class TaskProcessDataServiceImpl implements TaskMonitorDataService {
    private final String measurement;
    private final String uuid;
    private final String jsonStr;
    private final InfluxDBClient influxDBClient;
    private final RedisTemplate redisTemplate;

    public TaskProcessDataServiceImpl(String measurement, String uuid, String jsonStr) {
        this.measurement = measurement;
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.influxDBClient = ApplicationContextUtil.getBean(InfluxDBClient.class);
        this.redisTemplate = ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
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

            log.info("[Process 指标解析线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[Process-Error-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
