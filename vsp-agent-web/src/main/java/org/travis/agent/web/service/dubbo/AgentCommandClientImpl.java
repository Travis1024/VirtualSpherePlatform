package org.travis.agent.web.service.dubbo;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentCommandClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;

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
            R<String> stringR = VspRuntimeUtil.execForStr(command);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), stringR.getMsg()));
            return R.ok(stringR.getData());
        } catch (Exception e) {
            log.error("[HostCommandClientImpl::execSingleCommand] -> {}", e.toString());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
