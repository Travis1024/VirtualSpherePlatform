package org.travis.agent.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.dubbo.config.annotation.DubboReference;
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

    @Operation(summary = "Center-Dubbo健康检测")
    @GetMapping("/healthy")
    public String centerDubboHealthyCheck() {
        R<String> healthyCheck = centerMessageClient.dubboHealthyCheck();
        return healthyCheck.getData();
    }
}
