package org.travis.center.monitor.threads.basic;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * @ClassName TaskMonitorDataService
 * @Description 监控数据处理接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/29
 */
public interface TaskMonitorDataService extends Runnable{

    void flattenJsonNode(JsonNode node, String prefix, Map<String, Object> flatMap);

    void saveInfluxDB(Map<String, Object> flatMap, Long timestamp);

    void saveKeyToRedis(Map<String, Object> flatMap);

    void saveCacheToRedis(String str);
}
