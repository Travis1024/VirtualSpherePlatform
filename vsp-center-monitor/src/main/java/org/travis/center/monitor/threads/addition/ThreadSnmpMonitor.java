package org.travis.center.monitor.threads.addition;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.shared.common.constants.MonitorConstant;

import java.util.*;

/**
 * @ClassName ThreadSnmpMonitor
 * @Description snmp 数据监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Slf4j
public class ThreadSnmpMonitor implements Runnable{

    private final String measurement;
    private final String uuid;
    private final String jsonStr;
    private final InfluxDBClient influxDBClient;

    public ThreadSnmpMonitor(String measurement, String uuid, String jsonStr) {
        this.measurement = measurement;
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.influxDBClient = ApplicationContextUtil.getBean(InfluxDBClient.class);
    }

    public void saveInfluxDb(Map<String, Object> flatMap, Integer index, Long timestamp, String address) {
        Point point = Point
                .measurement(measurement)
                .addTag(MonitorConstant.INFLUX_TAG_ADDR, address)
                .addTag(MonitorConstant.INFLUX_TAG_INDEX, index.toString())
                .addFields(flatMap)
                .time(new Date(timestamp * 1000L).toInstant(), WritePrecision.MS);
        influxDBClient.getWriteApiBlocking().writePoint(point);
    }

    @Override
    public void run() {
        try {
            // 1、将 json 字符串转为 JsonNode 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNodeArray = objectMapper.readTree(jsonStr);

            // 2、遍历节点数组
            for (JsonNode rootNode : rootNodeArray) {
                // 2.1 获取当前记录时间
                long timestamp = rootNode.get("timestamp").asLong();
                String ip = rootNode.get("ip").asText();
                String port = rootNode.get("port").asText();

                // 2.2 处理 json 数据
                // 获取 oid_instance_map 数组处理节点
                JsonNode instanceMapNode = rootNode.get("oid_instance_map_list").get("oid_instance_map");
                ArrayNode instanceMapNodeArray = (ArrayNode) instanceMapNode;

                // 获取全量的数据 List
                List<JsonNode> arrayList = new ArrayList<>();
                for (JsonNode jsonNode : instanceMapNodeArray) {
                    arrayList.add(jsonNode);
                }

                int size = arrayList.size();
                int groupSize = (size / 10000) + 1;

                for (int i = 0; i < groupSize; i++) {
                    instanceMapNodeArray.removeAll();
                    for (int j = i * 10000; j < (i + 1) * 10000; j++) {
                        if (j >= size) {
                            break;
                        }

                        JsonNode oneNode = arrayList.get(j);
                        ObjectNode oneObjectNode = (ObjectNode) oneNode;
                        JsonNode oidInstance = oneNode.get("oid_instance");

                        oneObjectNode.put("name", oidInstance.get("name").asText());
                        oneObjectNode.put("oid", oidInstance.get("oid").asText());
                        oneObjectNode.remove("oid_instance");

                        instanceMapNodeArray.add(oneNode);
                    }
                    // 将数据存入 InfluxDB
                    Map<String, Object> fieldMap = new HashMap<>();
                    fieldMap.put("snmp", rootNode.toString());
                    saveInfluxDb(fieldMap, i, timestamp, ip + StrUtil.COLON + port);
                }
            }

            log.info("[SNMP 监测线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[Snmp-Error-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
