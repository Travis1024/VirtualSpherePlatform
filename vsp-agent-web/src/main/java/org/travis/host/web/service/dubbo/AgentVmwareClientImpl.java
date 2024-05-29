package org.travis.host.web.service.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName AgentVmwareClientImpl
 * @Description AgentVmwareClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Slf4j
@DubboService
public class AgentVmwareClientImpl implements AgentVmwareClient {
    @Override
    public R<Void> createVmware(String targetAgentIp, String xmlContent, Long vmwareId) {
        try {
            // TODO 创建虚拟机

            return R.ok();
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::createVmware] Agent Vmware Create Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
