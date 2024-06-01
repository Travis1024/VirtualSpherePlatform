package org.travis.api.client.center;

import org.travis.shared.common.domain.R;
import org.travis.shared.common.domain.WebSocketMessage;

/**
 * @ClassName CenterMessageClient
 * @Description CenterMessageClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
public interface CenterMessageClient {
    R<Void> sendGlobalMessage(WebSocketMessage webSocketMessage);

    R<String> dubboHealthyCheck();
}
