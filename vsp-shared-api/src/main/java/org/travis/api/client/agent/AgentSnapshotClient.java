package org.travis.api.client.agent;

import org.travis.api.pojo.bo.DiskBasicInfoBO;
import org.travis.api.pojo.dto.SnapshotBasicInfoDTO;
import org.travis.shared.common.domain.R;

import java.util.List;

/**
 * @ClassName AgentSnapshotClient
 * @Description AgentSnapshotClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
public interface AgentSnapshotClient {
    R<Void> mergeHistorySnapshot(String targetAgentIp, String vmwareUuid, String autoSnapshotName, String sharedStoragePath, List<SnapshotBasicInfoDTO> historySnapshotBasicInfoList);
    R<Void> createSnapshot(String targetAgentIp, String vmwareUuid, String autoSnapshotName);
    R<List<DiskBasicInfoBO>> queryDiskBasicInfo(String targetAgentIp, String vmwareUuid);
}
