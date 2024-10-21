package org.travis.center.monitor.threads.basic;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.shared.common.constants.MonitorConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName TaskNetDataServiceImpl
 * @Description Net-Stat 解析
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/29
 */
@Slf4j
public class TaskNetDataServiceImpl implements TaskMonitorDataService {
    private final String measurement;
    private final String uuid;
    private final String jsonStr;
    private final InfluxDBClient influxDBClient;
    private final RedisTemplate redisTemplate;

    public TaskNetDataServiceImpl(String measurement, String uuid, String jsonStr) {
        this.measurement = measurement;
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.influxDBClient = ApplicationContextUtil.getBean(InfluxDBClient.class);
        this.redisTemplate = ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
    }

    @Override
    public void flattenJsonNode(JsonNode node, String prefix, Map<String, Object> flatMap) {
        if (node.isObject()) {
            // --> 节点为对象
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                // connection_stats 节点跳过
                if (MonitorConstant.NET_CONNECTION_STATS.equals(entry.getKey())) continue;
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
        redisTemplate.opsForSet().add(MonitorConstant.REDIS_KEY_NET, flatMap.keySet().toArray());
    }

    @Override
    public void saveCacheToRedis(String str) {
        if (StrUtil.isEmpty(str)) return;
        Long size = redisTemplate.opsForList().size(MonitorConstant.CACHE_NET + MonitorConstant.M + uuid);
        if (size != null && size >= MonitorConstant.CACHE_LIMIT) {
            redisTemplate.opsForList().rightPop(MonitorConstant.CACHE_NET + MonitorConstant.M + uuid);
        }
        redisTemplate.opsForList().leftPush(MonitorConstant.CACHE_NET + MonitorConstant.M + uuid, str);
    }

    @Override
    public void run() {
        try {
            // 1、将 json 字符串转为 JsonNode 节点，并提取其中的 NetStat 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            long timestamp = rootNode.get(MonitorConstant.TIMESTAMP).asLong();
            JsonNode netStatNode = rootNode.get(MonitorConstant.NET_STAT);
            Map<String, Object> flatMap = new HashMap<>();
            // 2、单独处理 NetStat 节点中的 connection_stats 节点
            String connectionStats = objectMapper.writeValueAsString(netStatNode.get(MonitorConstant.NET_CONNECTION_STATS));
            flatMap.put(MonitorConstant.NET_STAT + "." + MonitorConstant.NET_CONNECTION_STATS, connectionStats);
            // 3、展平 json，转为 Map
            flattenJsonNode(netStatNode, MonitorConstant.NET_STAT + ".", flatMap);
            // 4、将 net-stat 所有的 key 缓存到 redis 中
            saveKeyToRedis(flatMap);
            // 5、将 Map 数据存入 influxDB
            saveInfluxDB(flatMap, timestamp);
            // 6、将 数据缓存到 redis 中，并删除旧的缓存数据
            JsonNode netIoCountersStats = netStatNode.get("net_io_counters_stats");
            ((ObjectNode) netIoCountersStats).put(MonitorConstant.TIMESTAMP, timestamp);
            String netIoCountersStatsStr = objectMapper.writeValueAsString(netIoCountersStats);
            saveCacheToRedis(netIoCountersStatsStr);

            log.info("[Net 指标解析线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[Net-Error-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
