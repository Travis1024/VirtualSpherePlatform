package org.travis.api.client.agent;

import org.travis.shared.common.domain.R;

/**
 * @ClassName AgentDiskClient
 * @Description AgentDiskClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/22
 */
public interface AgentDiskClient {
    R<String> createDisk(String targetAgentIp);
}
