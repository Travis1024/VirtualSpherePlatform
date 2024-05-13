package org.travis.center.manage.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.travis.api.client.host.HealthyClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;

/**
 * @ClassName DubboHostController
 * @Description DubboHostController
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
@RestController
@RequestMapping("/dubbo")
public class DubboHostController {
    @DubboReference
    private HealthyClient healthyClient;

    @Operation(summary = "Dubbo-通信检测")
    @GetMapping("/check")
    public String healthyCheck(@RequestParam("ip") String ip) {
        // PING Dubbo 请求
        try {
            R<String> healthyCheckR = healthyClient.healthyCheck(ip);
            if (healthyCheckR.checkFail()) {
                throw new DubboFunctionException(healthyCheckR.getMsg());
            }
            return healthyCheckR.getData();
        } catch (Exception e) {
            log.error("[{} - Dubbo Check Error] -> {}", ip, e.getMessage());
            throw new CommonException(BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getCode(), BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getMessage() + StrUtil.COLON + e.getMessage());
        }
    }
}
