package org.travis.center.web.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.center.CenterMessageClient;
import org.travis.center.support.websocket.WsMessageHolder;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.shared.common.enums.BizCodeEnum;

import javax.annotation.Resource;

/**
 * @ClassName CenterMessageClientImpl
 * @Description CenterMessageClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Slf4j
@DubboService
public class CenterMessageClientImpl implements CenterMessageClient {

    @Resource
    private WsMessageHolder wsMessageHolder;

    @Override
    public R<Void> sendGlobalMessage(WebSocketMessage webSocketMessage) {
        try {
            wsMessageHolder.sendGlobalMessage(webSocketMessage);
            return R.ok();
        } catch (Exception e) {
            log.error("[CenterMessageClientImpl::sendGlobalMessage] Send Global Message Error!");
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> dubboHealthyCheck() {
        return R.ok("Center Health Check OK!");
    }
}
