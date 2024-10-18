package org.travis.center.monitor.threads.basic;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName TaskCpuDataServiceImpl
 * @Description Cpu_Stat 解析
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Slf4j
public class TaskCpuDataServiceImpl implements TaskMonitorDataService {

    private String measurement;
    private String uuid;
    private String jsonStr;
    private InfluxDBClient influxDBClient;
    private RedisTemplate redisTemplate;
    private LogInfoService logInfoService;
    private ThreadPoolExecutor threadPoolExecutor;

    public TaskCpuDataServiceImpl(String measurement, String uuid, String jsonStr) {
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
        if (node.isObject()) {
            // --> 节点为对象
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                flattenJsonNode(entry.getValue(), prefix + entry.getKey() + ".", flatMap);
            }
        } else if (node.isArray()) {
            // --> 节点为数组
            for (int i = 0; i < node.size(); i++) {
                flattenJsonNode(node.get(i), prefix + i + ".", flatMap);
            }
        } else {
            // --> 叶子节点
            flatMap.put(prefix.substring(0, prefix.length() - 1), node.asText());
        }
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
        redisTemplate.opsForSet().add(MonitorConstant.REDIS_KEY_CPU, flatMap.keySet().toArray());
    }

    @Override
    public void saveCacheToRedis(String str) {
        if (StrUtil.isEmpty(str)) return;
        Long size = redisTemplate.opsForList().size(MonitorConstant.CACHE_CPU + MonitorConstant.M + uuid);
        if (size != null && size >= MonitorConstant.CACHE_LIMIT) {
            redisTemplate.opsForList().rightPop(MonitorConstant.CACHE_CPU + MonitorConstant.M + uuid);
        }
        redisTemplate.opsForList().leftPush(MonitorConstant.CACHE_CPU + MonitorConstant.M + uuid, str);

    }

    @Override
    public void sendWebSocket(String message) {
        WebSocketMonitorData.sendCpuMessage(uuid, message);
    }

    @Override
    public void run() {
        try {
            // 1、将 json 字符串转为 JsonNode 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            long timestamp = rootNode.get(MonitorConstant.TIMESTAMP).asLong();
            JsonNode cpuStatNode = rootNode.get(MonitorConstant.CPU_STAT);
            HashMap<String, Object> flatMap = new HashMap<>();
            // 2、单独处理，将 cpu_time_stats 整体放入 map 中
            String cpuTimeStats = objectMapper.writeValueAsString(cpuStatNode.get(MonitorConstant.CPU_TIME_STATS));
            flatMap.put(MonitorConstant.CPU_STAT + "." + MonitorConstant.CPU_TIME_STATS, cpuTimeStats);
            // 3、展平 json，转为 map
            flattenJsonNode(cpuStatNode, MonitorConstant.CPU_STAT + ".", flatMap);
            // 4、将 cpu-stat 所有的 key 缓存到 redis 中
            saveKeyToRedis(flatMap);
            // 5、将 Map 数据存入 influxDB
            saveInfluxDB(flatMap, timestamp);
            // 6、将数据通过 WebSocket 推送给前端
            ((ObjectNode) cpuStatNode).remove(MonitorConstant.CPU_TIME_STATS);
            ((ObjectNode) cpuStatNode).put(MonitorConstant.TIMESTAMP, timestamp);
            String cpuCacheStatsStr = objectMapper.writeValueAsString(cpuStatNode);
            sendWebSocket(cpuCacheStatsStr);
            // 7、将数据缓存到 redis 中，并删除旧的缓存数据
            saveCacheToRedis(cpuCacheStatsStr);

            log.info("[CPU 指标解析线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.CPU_THREAD, true));

        } catch (Exception e) {
            log.error("[CPU-Error]" + e);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.CPU_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }

    }
}
