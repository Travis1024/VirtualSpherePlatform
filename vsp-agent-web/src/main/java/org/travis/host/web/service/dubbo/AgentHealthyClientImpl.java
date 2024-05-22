package org.travis.host.web.service.dubbo;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentHealthyClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName HealthyClientImpl
 * @Description HealthyClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
@DubboService
public class AgentHealthyClientImpl implements AgentHealthyClient {
    @Override
    public R<String> healthyCheck(String targetAgentIp) {
        try {
            log.info("[HealthyClientImpl::healthyCheck] Healthy Check Success! -> {}", DateUtil.date());
            return R.ok(DateUtil.date().toString());
        } catch (Exception e) {
            log.error("[HealthyClientImpl::healthyCheck] Healthy Check Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
