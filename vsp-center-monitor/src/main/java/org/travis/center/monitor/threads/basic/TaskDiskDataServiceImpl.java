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
import org.travis.center.monitor.pojo.vo.DiskStatsResultVO;
import org.travis.center.monitor.pojo.vo.DiskStatsTempVO;
import org.travis.shared.common.constants.MonitorConstant;

import java.util.*;

/**
 * @ClassName TaskDiskDataServiceImpl
 * @Description Disk 异步监测任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Slf4j
public class TaskDiskDataServiceImpl implements TaskMonitorDataService {
    private final String measurement;
    private final String uuid;
    private final String jsonStr;
    private final InfluxDBClient influxDBClient;
    private final RedisTemplate redisTemplate;

    public TaskDiskDataServiceImpl(String measurement, String uuid, String jsonStr) {
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
        redisTemplate.opsForSet().add(MonitorConstant.REDIS_KEY_DISK, flatMap.keySet().toArray());
    }

    @Override
    public void saveCacheToRedis(String str) {
        if (StrUtil.isEmpty(str)) return;
        Long size = redisTemplate.opsForList().size(MonitorConstant.CACHE_DISK + MonitorConstant.M + uuid);
        if (size != null && size >= MonitorConstant.CACHE_LIMIT) {
            redisTemplate.opsForList().rightPop(MonitorConstant.CACHE_DISK + MonitorConstant.M + uuid);
        }
        redisTemplate.opsForList().leftPush(MonitorConstant.CACHE_DISK + MonitorConstant.M + uuid, str);
    }

    private void forArrayHandle(JsonNode partitionWithUsageStatsNode, List<DiskStatsTempVO> list) {
        for (int i = 0; i < partitionWithUsageStatsNode.size(); i++) {
            JsonNode node = partitionWithUsageStatsNode.get(i);
            DiskStatsTempVO diskStatsTempVO = new DiskStatsTempVO();
            diskStatsTempVO.setDevice(node.get(MonitorConstant.DISK_DEVICE).asText());
            diskStatsTempVO.setMountpoint(node.get(MonitorConstant.DISK_MOUNT_POINT).asText());
            diskStatsTempVO.setUsedPercent(node.get(MonitorConstant.DISK_USED_PERCENT).asText());
            list.add(diskStatsTempVO);
        }
    }

    @Override
    public void run() {
        try {
            // 1、将 json 字符串转为 JsonNode 节点，并提起其中的 DiskStat 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            long timestamp = rootNode.get(MonitorConstant.TIMESTAMP).asLong();
            JsonNode diskStatNode = rootNode.get(MonitorConstant.DISK_STAT);
            Map<String, Object> flatMap = new HashMap<>();
            // 2、获取 partition_with_usage_stats 节点
            JsonNode partitionWithUsageStatsNode = diskStatNode.get(MonitorConstant.DISK_PARTITION_WITH_USAGE_STATS);
            String partitionWithUsageStatsStr = objectMapper.writeValueAsString(partitionWithUsageStatsNode);
            flatMap.put(MonitorConstant.DISK_STAT + "." + MonitorConstant.DISK_PARTITION_WITH_USAGE_STATS, partitionWithUsageStatsStr);
            // 3、将 key 缓存到 redis 中
            saveKeyToRedis(flatMap);
            // 4、将 Map 数据存入 influxDB
            saveInfluxDB(flatMap, timestamp);
            // 5、将数据缓存到 redis 中，并删除旧的缓存数据
            List<DiskStatsTempVO> list = new ArrayList<>();
            forArrayHandle(partitionWithUsageStatsNode, list);
            String result = JSONUtil.toJsonStr(new DiskStatsResultVO(list, timestamp));
            saveCacheToRedis(result);

            log.info("[Disk 指标解析线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[Disk-Error-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
