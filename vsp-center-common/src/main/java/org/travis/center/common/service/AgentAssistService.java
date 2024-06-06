package org.travis.center.common.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.shared.common.constants.SystemConstant;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    @Resource
    private HostInfoMapper hostInfoMapper;
    @Value("${vsp.host.shared.storagePath}")
    private String hostSharedStoragePath;

    public List<String> getHealthyHostAgentIpList() {
        List<String> ipList = new ArrayList<>();
        try {
            // 1.从 Zookeeper 获取 Agent 注册列表
            List<String> serverList = curatorFramework.getChildren().forPath(SystemConstant.ZOOKEEPER_HOST_SERVER_PATH);
            Set<String> zookeeperAgentSet = serverList.stream().map(server -> server.split(StrUtil.COLON)[0].trim()).collect(Collectors.toSet());
            if (zookeeperAgentSet.isEmpty()) {
                return ipList;
            }
            // 2.从数据库中获取有效的 HostInfo 列表
            List<String> hostList = hostInfoMapper.selectList(
                    Wrappers.<HostInfo>lambdaQuery().select(HostInfo::getIp)
                    )
                    .stream()
                    .map(hostInfo -> hostInfo.getIp().trim())
                    .collect(Collectors.toList());
            if (hostList.isEmpty()) {
                return ipList;
            }
            // 3.计算 Agent 注册列表与 HostInfo 列表的交集
            ipList = hostList.stream().filter(zookeeperAgentSet::contains).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[AgentAssistService::getHealthyHostAgentIpList] Error: {}", e.getMessage());
        }
        return ipList;
    }

    public String getHostSharedStoragePath() {
        return hostSharedStoragePath.endsWith(File.separator) ? hostSharedStoragePath.substring(0, hostSharedStoragePath.length() - File.separator.length()) : hostSharedStoragePath;
    }
}
