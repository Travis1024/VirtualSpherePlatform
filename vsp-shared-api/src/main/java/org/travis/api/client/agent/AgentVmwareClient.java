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

    R<String> startVmware(String targetAgentIp, String vmwareUuid);

    R<String> suspendVmware(String targetAgentIp, String vmwareUuid);

    R<String> complexVmware(String targetAgentIp, String vmwareUuid, String subParam);

    R<String> resumeVmware(String targetAgentIp, String vmwareUuid);

    R<String> shutdownVmware(String targetAgentIp, String vmwareUuid);

    R<String> destroyVmware(String targetAgentIp, String vmwareUuid);

    R<String> undefineVmware(String targetAgentIp, String vmwareUuid);

    R<Void> modifyVmwareMemory(String targetAgentIp, String vmwareUuid, Long memory, boolean isPowerOff);

    R<Void> modifyVmwareVcpu(String targetAgentIp, String vmwareUuid, Integer vcpuNumber, boolean isPowerOff);

    R<String> queryVncAddress(String targetAgentIp, String vmwareUuid);

    R<String> queryIpAddress(String targetAgentIp, String uuid, String netRange);
}
