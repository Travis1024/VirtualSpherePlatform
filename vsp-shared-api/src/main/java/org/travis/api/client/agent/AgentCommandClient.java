package org.travis.api.client.agent;

import org.travis.shared.common.domain.R;

/**
 * @ClassName AgentCommandClient
 * @Description AgentCommandClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
public interface AgentCommandClient {

    R<String> execSingleCommand(String targetAgentIp, String command);
}
