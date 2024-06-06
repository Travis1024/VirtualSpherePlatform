package org.travis.center.common.config.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ZookeeperConfig
 * @Description ZookeeperConfig
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Slf4j
@Configuration
public class ZookeeperConfig {
    @Value("${dubbo.registry.address}")
    private String zookeeperServer;

    @Bean
    public CuratorFramework curatorFramework() {
        String zookeeperAddr = zookeeperServer.substring("zookeeper://".length());
        log.info("[Zookeeper Client Register] -> {}", zookeeperAddr);

        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddr)
                .sessionTimeoutMs(30000)
                .retryPolicy(new ExponentialBackoffRetry(2000, 3))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }
}
