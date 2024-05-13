package org.travis.api.client.host;

import org.travis.shared.common.domain.R;

/**
 * @ClassName HealthyClient
 * @Description HealthyClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface HealthyClient {

    R<String> healthyCheck(String targetHostIp);

}
