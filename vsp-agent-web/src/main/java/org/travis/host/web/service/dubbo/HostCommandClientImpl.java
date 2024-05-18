package org.travis.host.web.service.dubbo;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.host.HostCommandClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

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
    public R<String> execSingleCommand(String targetHostIp, String command) {
        try {
            String result = RuntimeUtil.execForStr(command);
            log.info("[{}] -> {}", command, result);
            int a = 1 / 0;
            return R.ok(result);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
