package org.travis.agent.web.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.travis.api.client.center.CenterMessageClient;
import org.travis.shared.common.domain.R;

/**
 * @ClassName DubboController
 * @Description DubboController
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@RestController
@RequestMapping("/dubbo")
public class DubboController {
    @DubboReference
    private CenterMessageClient centerMessageClient;
    @Value("${dubbo.provider.host:}")
    private String dubboProviderHost;

    @Operation(summary = "Center-Dubbo健康检测")
    @GetMapping("/healthy")
    public String centerDubboHealthyCheck() {
        R<String> healthyCheck = centerMessageClient.dubboHealthyCheck();
        return healthyCheck.getData();
    }

    @Operation(summary = "查询向Zookeeper注册的IP地址")
    @GetMapping("/dubboIp")
    public String getDubboProviderIpAddr() {
        return StrUtil.isNotEmpty(dubboProviderHost) ? dubboProviderHost : NetUtils.getLocalHost();
    }
}
