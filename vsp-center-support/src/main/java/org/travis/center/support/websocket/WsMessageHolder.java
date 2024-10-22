package org.travis.center.support.websocket;

import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.GlobalMessage;
import org.travis.shared.common.enums.MsgConfirmEnum;
import org.travis.center.common.mapper.support.GlobalMessageMapper;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.center.support.websocket.service.WsGlobalMessage;
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
    public GlobalMessageMapper globalMessageMapper;

    public void sendGlobalMessage(WebSocketMessage webSocketMessage) {
        // 1.推送消息
        WsGlobalMessage.sendGlobalMsg(JSONUtil.toJsonStr(webSocketMessage));
        // 2.消息持久化
        messagePersistence(webSocketMessage);
    }

    private void messagePersistence(WebSocketMessage webSocketMessage) {
        GlobalMessage globalMessage = new GlobalMessage();
        globalMessage.setId(SnowflakeIdUtil.nextId());
        globalMessage.setIsConfirm(MsgConfirmEnum.UN_CONFIRMED);
        globalMessage.setMessageTitle(webSocketMessage.getMsgTitle());
        globalMessage.setMessageContent(webSocketMessage.getMsgContent());
        globalMessage.setMessageState(webSocketMessage.getMsgState());
        globalMessage.setMessageModule(webSocketMessage.getMsgModule());
        if (webSocketMessage.getNodeMachineType() != null) {
            globalMessage.setNodeMachineType(webSocketMessage.getNodeMachineType());
        }
        if (webSocketMessage.getNodeMachineUuid() != null) {
            globalMessage.setNodeMachineUuid(webSocketMessage.getNodeMachineUuid());
        }
        globalMessageMapper.insert(globalMessage);
    }
}
