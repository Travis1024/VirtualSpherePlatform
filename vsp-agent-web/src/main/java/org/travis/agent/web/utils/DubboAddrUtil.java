package org.travis.agent.web.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName DubboAddrUtil
 * @Description DubboAddrUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Slf4j
@Component
public class DubboAddrUtil {
    @Value("${dubbo.provider.host}")
    private String dubboProviderHost;

    public String getRegisterToDubboIpAddr() {
        return StrUtil.isNotEmpty(dubboProviderHost) ? dubboProviderHost : NetUtils.getLocalHost();
    }
}
