package org.travis.center.monitor.threads.addition;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.shared.common.constants.MonitorConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ThreadIpmiSensorMonitor
 * @Description ipmi 传感器数据监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Slf4j
public class ThreadIpmiSensorMonitor implements Runnable{

    private String measurement;
    private String uuid;
    private String jsonStr;
    private InfluxDBClient influxDBClient;

    public ThreadIpmiSensorMonitor(String measurement, String uuid, String jsonStr) {
        this.measurement = measurement;
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.influxDBClient = ApplicationContextUtil.getBean(InfluxDBClient.class);
    }

    public void saveInfluxDb(Map<String, Object> flatMap, Long timestamp, String address) {
        Point point = Point
                .measurement(measurement)
                .addTag(MonitorConstant.INFLUX_TAG_ADDR, address)
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

                // 2.2 将数据存入 InfluxDB
                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("ipmi", rootNode.toString());
                saveInfluxDb(fieldMap, timestamp, ip + StrUtil.COLON + port);
            }

            log.info("[IPMI-Sensor 监测线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[IPMI-Sensor-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
