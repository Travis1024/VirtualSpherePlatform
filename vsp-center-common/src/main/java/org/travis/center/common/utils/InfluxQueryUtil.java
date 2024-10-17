package org.travis.center.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName InfluxQueryUtil
 * @Description Influx查询工具类
 * @Author travis-wei
 * @Version v1.0
 */
@Component
public class InfluxQueryUtil {

    @Value("${influx.bucket}")
    private String bucket;

    @Value("${influx.measurement}")
    private String measurement;

    public String getAggregationStr() {
        return  "from(bucket: \"" + bucket + "\")\n" +
                "  |> range(start: %s, stop: %s)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement + "\")\n" +
                "  |> filter(fn: (r) => r[\"_field\"] == \"%s\")\n" +
                "  |> filter(fn: (r) => r[\"uuid\"] == \"%s\")\n" +
                "  |> aggregateWindow(every: %s, fn: %s, createEmpty: false)";
    }

    public String getPageQueryStr() {
        return "from(bucket: \"" + bucket + "\")\n" +
                "  |> range(start: %s, stop: %s)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement + "\")\n" +
                "  |> filter(fn: (r) => r[\"_field\"] == \"%s\")\n" +
                "  |> filter(fn: (r) => r[\"uuid\"] == \"%s\")\n" +
                "  |> limit(n:%s, offset: %s)";
    }

    public String getAddrAggregationStr() {
        return  "from(bucket: \"" + bucket + "\")\n" +
                "  |> range(start: %s, stop: %s)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement + "\")\n" +
                "  |> filter(fn: (r) => r[\"_field\"] == \"%s\")\n" +
                "  |> filter(fn: (r) => r[\"addr\"] == \"%s\")\n" +
                "  |> aggregateWindow(every: %s, fn: %s, createEmpty: false)";
    }

    public String getAddrPageQueryStr() {
        return "from(bucket: \"" + bucket + "\")\n" +
                "  |> range(start: %s, stop: %s)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement + "\")\n" +
                "  |> filter(fn: (r) => r[\"_field\"] == \"%s\")\n" +
                "  |> filter(fn: (r) => r[\"addr\"] == \"%s\")\n" +
                "  |> limit(n:%s, offset: %s)";
    }
}
