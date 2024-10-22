package org.travis.api.client.center;

import org.travis.shared.common.domain.R;

/**
 * @ClassName CenterVmwareClient
 * @Description CenterVmwareClient
 * @Author Travis
 * @Data 2024/10
 */
public interface CenterVmwareClient {
    R<String> queryIpAddr(Long vmwareId);
}
