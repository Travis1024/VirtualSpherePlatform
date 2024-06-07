package org.travis.api.client.center;

/**
 * @ClassName CenterHostClient
 * @Description CenterHostClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
public interface CenterHostClient {
    void sendBridgedInitResultMessage(Long hostId, String hostName, boolean isSuccess, String stateMessage);
}
