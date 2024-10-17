package org.travis.center.common.config.influxdb;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName InfluxdbConfig
 * @Description InfluxDB 配置类
 * @Author travis-wei
 * @Version v1.0
 */
@Configuration
public class InfluxdbConfig {
    @Value("${influx.url}")
    private String influxdbUrl;
    @Value("${influx.token}")
    private String token;
    @Value("${influx.bucket}")
    private String bucket;
    @Value("${influx.org}")
    private String org;

    @Bean
    public InfluxDBClient influxDBClient() {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(influxdbUrl, token.toCharArray(), org, bucket);
        influxDBClient.setLogLevel(LogLevel.BASIC);
        return influxDBClient;
    }

}
