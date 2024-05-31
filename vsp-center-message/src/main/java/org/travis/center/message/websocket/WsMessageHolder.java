package org.travis.center.message.websocket;

import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.message.GlobalMessage;
import org.travis.shared.common.enums.MsgConfirmEnum;
import org.travis.center.common.mapper.message.GlobalMessageMapper;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.center.message.websocket.service.WsGlobalMsg;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;

/**
 * @ClassName WsGlobalMsgProxy
 * @Description WsGlobalMsgProxy
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Component
public class WsMessageHolder {
    @Resource
    private GlobalMessageMapper globalMessageMapper;

    public void sendGlobalMessage(WebSocketMessage webSocketMessage) {
        // 1.推送消息
        WsGlobalMsg.sendGlobalMsg(JSONUtil.toJsonStr(webSocketMessage));
        // 2.消息持久化
        messagePersistence(webSocketMessage);
    }

    private void messagePersistence(WebSocketMessage webSocketMessage) {
        GlobalMessage globalMessage = new GlobalMessage();
        globalMessage.setId(SnowflakeIdUtil.nextId());
        globalMessage.setIsConfirm(MsgConfirmEnum.UN_CONFIRMED);
        globalMessage.setMessageContent(webSocketMessage.getMsgContent());
        globalMessage.setMessageState(webSocketMessage.getMsgState());
        globalMessage.setMessageModule(webSocketMessage.getMsgModule());
        globalMessageMapper.insert(globalMessage);
    }
}
