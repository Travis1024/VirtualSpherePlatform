package org.travis.agent.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.travis.agent.web.utils.DubboAddrUtil;
import org.travis.api.client.center.CenterHealthyClient;
import org.travis.shared.common.domain.R;

import javax.annotation.Resource;

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
    public CenterHealthyClient centerHealthyClient;
    @Resource
    public DubboAddrUtil dubboAddrUtil;

    @Operation(summary = "Center-Dubbo健康检测")
    @GetMapping("/healthy")
    public String centerDubboHealthyCheck() {
        R<Void> healthyCheck = centerHealthyClient.dubboHealthyCheck();
        return healthyCheck.getMsg();
    }

    @Operation(summary = "查询向Zookeeper注册的IP地址")
    @GetMapping("/dubboIp")
    public String getRegisterToDubboIpAddr() {
        return dubboAddrUtil.getRegisterToDubboIpAddr();
    }
}
