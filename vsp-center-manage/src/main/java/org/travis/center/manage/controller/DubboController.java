package org.travis.center.manage.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.travis.api.client.agent.AgentHealthyClient;
import org.travis.center.support.aspect.Log;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;

/**
 * @ClassName DubboController
 * @Description DubboController
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
@RestController
@RequestMapping("/dubbo")
public class DubboController {

    @DubboReference
    public AgentHealthyClient agentHealthyClient;

    @Log(title = "Dubbo-通信检测")
    @Operation(summary = "Dubbo-通信检测")
    @GetMapping("/check")
    public String healthyCheck(@RequestParam("ip") String ip) {
        try {
            R<String> healthyCheckR = agentHealthyClient.healthyCheck(ip);
            Assert.isFalse(healthyCheckR.checkFail(), () -> new DubboFunctionException(healthyCheckR.getMsg()));
            return healthyCheckR.getData();
        } catch (Exception e) {
            log.error("[DubboHostController::healthyCheck] {} - Dubbo Check Error! -> {}", ip, e.getMessage());
            throw new CommonException(BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getCode(), BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getMessage() + StrUtil.COLON + e.getMessage());
        }
    }
}
