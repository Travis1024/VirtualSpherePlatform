package org.travis.api.client.agent.callback;

/**
 * @ClassName HostCallbackListener
 * @Description HostCallbackListener
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/6
 */
public interface HostCallbackListener {
    void sendBridgedInitResultMessage(Long hostId, boolean isSuccess, String stateMessage);
}
