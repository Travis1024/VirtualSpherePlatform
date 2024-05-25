package org.travis.api.client.center;

import org.travis.shared.common.domain.R;

/**
 * @ClassName CenterHostClient
 * @Description CenterHostClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
public interface CenterHostClient {
    R<Void> sendBridgedInitMessage(Long hostId, boolean isSuccess, String stateMessage);
}
