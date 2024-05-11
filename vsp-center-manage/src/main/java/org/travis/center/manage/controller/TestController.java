package org.travis.center.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.travis.api.client.host.HostCommandClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboCallException;
import org.travis.shared.common.exceptions.DubboFunctionException;

/**
 * @ClassName TestController
 * @Description TestController
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
@Slf4j
@RestController
@RequestMapping("/manage/test")
public class TestController {

    @DubboReference
    private HostCommandClient hostCommandClient;

    @Operation(summary = "发送 dubbo 测试")
    @GetMapping("/send")
    public String sendCommand(String targetHostIp, String command) {
        log.info("{} -> {}", targetHostIp, command);
        R<String> execked = hostCommandClient.execSingleCommand(targetHostIp, command);
        if (execked.checkFail()) {
            throw new DubboFunctionException(execked.getCode(), execked.getMsg());
        }
        return execked.getData();
    }
}
