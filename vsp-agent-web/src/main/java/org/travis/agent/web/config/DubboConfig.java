package org.travis.agent.web.config;

import org.apache.dubbo.config.ProtocolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DubboConfig
 * @Description TODDubboConfigO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/8
 */
@Configuration
public class DubboConfig {

    @Value("${dubbo.protocol.name}")
    private String dubboProtocolName;
    @Value("${dubbo.protocol.port}")
    private Integer dubboProtocolPort;

    @Bean
    public ProtocolConfig getProtocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(dubboProtocolName);
        protocolConfig.setPort(dubboProtocolPort);
        return protocolConfig;
    }
}
