package org.travis.center.monitor.threads.basic;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.platform.common.constants.MonitorConstant;
import com.hust.platform.common.constants.StatisticConstant;
import com.hust.platform.common.pojo.monitor.vo.DiskStatsResultVO;
import com.hust.platform.common.pojo.monitor.vo.DiskStatsTempVO;
import com.hust.platform.common.utils.ApplicationContextUtil;
import com.hust.platform.common.websocket.WebSocketMonitorData;
import com.hust.platform.logger.service.LogInfoService;
import com.hust.platform.logger.threads.TaskThreadNumberStatistic;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName TaskDiskDataServiceImpl
 * @Description Disk 异步监测任务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Slf4j
public class TaskDiskDataServiceImpl implements TaskMonitorDataService {
    private String measurement;
    private String uuid;
    private String jsonStr;
    private InfluxDBClient influxDBClient;
    private RedisTemplate redisTemplate;
    private LogInfoService logInfoService;
    private ThreadPoolExecutor threadPoolExecutor;

    public TaskDiskDataServiceImpl(String measurement, String uuid, String jsonStr) {
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

    @Override
    public void sendWebSocket(String message) {
        WebSocketMonitorData.sendDiskMessage(uuid, message);
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
            // 5、将数据通过 WebSocket 推送给前端
            List<DiskStatsTempVO> list = new ArrayList<>();
            forArrayHandle(partitionWithUsageStatsNode, list);
            String result = JSONUtil.toJsonStr(new DiskStatsResultVO(list, timestamp));
            sendWebSocket(result);
            // 6、将数据缓存到 redis 中，并删除旧的缓存数据
            saveCacheToRedis(result);

            log.info("[Disk 指标解析线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.DISK_THREAD, true));
        } catch (Exception e) {
            log.error("[Disk-Error]" + e);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.DISK_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }
    }
}
