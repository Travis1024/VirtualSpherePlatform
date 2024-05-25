package org.travis.api.client.agent;

import org.travis.api.pojo.bo.HostDetailsBO;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.shared.common.domain.R;

/**
 * @ClassName AgentHostClient
 * @Description AgentHostClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/24
 */
public interface AgentHostClient {
    R<HostDetailsBO> queryHostInfoDetails(String targetAgentIp);
    R<Void> execBridgedAdapter(String targetAgentIp, HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO);
}
