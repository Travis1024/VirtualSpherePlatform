package org.travis.host.web.service.dubboImpl;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.host.HostCommandClient;

/**
 * @ClassName HostCommandClient
 * @Description HostCommandClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
@Slf4j
@DubboService
public class HostCommandClientImpl implements HostCommandClient {
    @Override
    public String execSingleCommand(String targetHostIp, String command) {
        String result = RuntimeUtil.execForStr(command);
        log.info("[{}] -> {}", command, result);
        return result;
    }
}
