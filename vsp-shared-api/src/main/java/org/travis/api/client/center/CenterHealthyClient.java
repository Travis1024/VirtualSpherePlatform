package org.travis.api.client.center;

import org.travis.api.pojo.bo.HostHealthyStateBO;
import org.travis.shared.common.domain.R;

/**
 * @ClassName CenterHealthyClient
 * @Description CenterHealthyClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
public interface CenterHealthyClient {
    R<Void> dubboHealthyCheck();
    R<Void> pushHostHealthyState(HostHealthyStateBO hostHealthyStateBO);
}
