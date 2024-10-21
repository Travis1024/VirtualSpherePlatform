package org.travis.center.monitor.threads.basic;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.center.monitor.pojo.vo.MemStatsResultVO;
import org.travis.shared.common.constants.MonitorConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName TaskMemDataServiceImpl
 * @Description Memory 异步监测任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Slf4j
public class TaskMemDataServiceImpl implements TaskMonitorDataService {

    private final String measurement;
    private final String uuid;
    private final String jsonStr;
    private final InfluxDBClient influxDBClient;
    private final RedisTemplate redisTemplate;

    public TaskMemDataServiceImpl(String measurement, String uuid, String jsonStr) {
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
        redisTemplate.opsForSet().add(MonitorConstant.REDIS_KEY_MEM, flatMap.keySet().toArray());
    }

    @Override
    public void saveCacheToRedis(String str) {
        if (StrUtil.isEmpty(str)) return;
        Long size = redisTemplate.opsForList().size(MonitorConstant.CACHE_MEM + MonitorConstant.M + uuid);
        if (size != null && size >= MonitorConstant.CACHE_LIMIT) {
            redisTemplate.opsForList().rightPop(MonitorConstant.CACHE_MEM + MonitorConstant.M + uuid);
        }
        redisTemplate.opsForList().leftPush(MonitorConstant.CACHE_MEM + MonitorConstant.M + uuid, str);
    }

    @Override
    public void run() {
        try {
            // 1、将 json 字符串转为 JsonNode 节点，并提取其中的 MemStat 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            long timestamp = rootNode.get(MonitorConstant.TIMESTAMP).asLong();
            JsonNode memStatNode = rootNode.get(MonitorConstant.MEM_STAT);
            HashMap<String, Object> flatMap = new HashMap<>();
            // 2、展平 json，转为 Map
            flattenJsonNode(memStatNode, MonitorConstant.MEM_STAT + ".", flatMap);
            // 3、将 mem-stat 所有的 key 缓存到 redis 中
            saveKeyToRedis(flatMap);
            // 4、将 Map 数据存入 influxDB
            saveInfluxDB(flatMap, timestamp);
            // 5、将数据缓存到 redis 中，并删除旧的缓存数据
            String usedPercent = (String) flatMap.get(MonitorConstant.MEM_STAT + "." + MonitorConstant.MEM_SWAP_MEMORY_STAT + "." + MonitorConstant.MEM_USED_PERCENT);
            String total = (String) flatMap.get(MonitorConstant.MEM_STAT + "." + MonitorConstant.MEM_VIRTUAL_MEMORY_STAT + "." + MonitorConstant.MEM_TOTAL);
            String available = (String) flatMap.get(MonitorConstant.MEM_STAT + "." + MonitorConstant.MEM_VIRTUAL_MEMORY_STAT + "." + MonitorConstant.MEM_AVAILABLE);

            MemStatsResultVO resultVO = new MemStatsResultVO();
            resultVO.setSwapMemoryUsedPercent(usedPercent);
            resultVO.setTimestamp(timestamp);

            if (!StrUtil.isEmpty(total) && !StrUtil.isEmpty(available)) {
                double a = Double.parseDouble(available);
                double t = Double.parseDouble(total);
                resultVO.setVirtualMemoryUsedPercent(String.valueOf((t - a) * 100.0 / t));
            } else {
                resultVO.setVirtualMemoryUsedPercent("0");
            }
            String result = JSONUtil.toJsonStr(resultVO);
            saveCacheToRedis(result);

            log.info("[Memory 指标解析线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[Memory-Error-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
