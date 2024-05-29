package org.travis.api.client.agent;

import org.travis.shared.common.domain.R;

/**
 * @ClassName AgentVmwareClient
 * @Description AgentVmwareClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
public interface AgentVmwareClient {
    R<Void> createVmware(String targetAgentIp, String xmlContent, Long vmwareId);
}
