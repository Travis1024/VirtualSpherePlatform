package org.travis.api.client.agent;

import org.travis.shared.common.domain.R;

/**
 * @ClassName AgentImageClient
 * @Description AgentImageClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/23
 */
public interface AgentImageClient {
    R<String> deleteImage(String targetAgentIp, String path);
}
