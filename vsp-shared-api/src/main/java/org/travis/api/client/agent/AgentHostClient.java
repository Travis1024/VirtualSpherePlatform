package org.travis.api.client.agent;

import org.travis.api.pojo.bo.HostDetailsBO;
import org.travis.api.pojo.bo.HostResourceInfoBO;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.shared.common.domain.R;

import java.util.List;

/**
 * @ClassName AgentHostClient
 * @Description AgentHostClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/24
 */
public interface AgentHostClient {
    R<HostDetailsBO> queryHostInfoDetails(String targetAgentIp);
    void execBridgedAdapter(String targetAgentIp, HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO);
    R<HostResourceInfoBO> queryHostResourceInfo(String targetAgentIp);
    R<Void> deleteFiles(String targetAgentIp, List<String> filePaths);
}
