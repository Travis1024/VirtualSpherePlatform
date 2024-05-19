package org.travis.center.common.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;
import org.travis.shared.common.constants.SystemConstant;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName AgentAssistService
 * @Description AgentAssistService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/19
 */
@Slf4j
@Service
public class AgentAssistService {
    @Resource
    private CuratorFramework curatorFramework;

    public List<String> getHealthyHostAgentIpList() {
        List<String> ipList = new ArrayList<>();
        try {
            List<String> serverList = curatorFramework.getChildren().forPath(SystemConstant.ZOOKEEPER_HOST_SERVER_PATH);
            if (serverList != null && !serverList.isEmpty()) {
                ipList = serverList.stream().map(server -> server.split(StrUtil.COLON)[0]).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("[GetHealthyHostAgentIpList] Error: {}", e.getMessage());
        }
        return ipList;
    }
}
