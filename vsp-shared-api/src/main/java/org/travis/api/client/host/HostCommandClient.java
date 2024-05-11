package org.travis.api.client.host;

import org.travis.shared.common.domain.R;

/**
 * @ClassName HostCommandClient
 * @Description HostCommandClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
public interface HostCommandClient {

    R<String> execSingleCommand(String targetHostIp, String command);
}
