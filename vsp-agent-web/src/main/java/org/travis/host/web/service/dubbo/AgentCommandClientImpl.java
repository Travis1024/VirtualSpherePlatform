package org.travis.host.web.service.dubbo;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentCommandClient;
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
public class AgentCommandClientImpl implements AgentCommandClient {
    @Override
    public R<String> execSingleCommand(String targetAgentIp, String command) {
        try {
            String result = RuntimeUtil.execForStr(command);
            return R.ok(result);
        } catch (Exception e) {
            log.error("[HostCommandClientImpl::execSingleCommand] -> {}", e.toString());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
