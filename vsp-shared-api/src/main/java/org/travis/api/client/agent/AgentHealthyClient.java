package org.travis.api.client.agent;

import org.travis.shared.common.domain.R;

/**
 * @ClassName AgentHealthyClient
 * @Description AgentHealthyClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface AgentHealthyClient {

    R<String> healthyCheck(String targetAgentIp);
}
