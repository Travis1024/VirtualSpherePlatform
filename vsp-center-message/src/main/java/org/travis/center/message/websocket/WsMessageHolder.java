package org.travis.center.message.websocket;

import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.message.GlobalMessage;
import org.travis.center.common.enums.MsgConfirmEnum;
import org.travis.center.common.mapper.message.GlobalMessageMapper;
import org.travis.center.message.pojo.vo.WsMessageVO;
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

    public void sendGlobalMessage(WsMessageVO wsMessageVO) {
        // 1.推送消息
        WsGlobalMsg.sendGlobalMsg(JSONUtil.toJsonStr(wsMessageVO));
        // 2.消息持久化
        messagePersistence(wsMessageVO);
    }

    private void messagePersistence(WsMessageVO wsMessageVO) {
        GlobalMessage globalMessage = new GlobalMessage();
        globalMessage.setId(SnowflakeIdUtil.nextId());
        globalMessage.setIsConfirm(MsgConfirmEnum.UN_CONFIRMED);
        globalMessage.setMessageContent(wsMessageVO.getMsgContent());
        globalMessage.setMessageState(wsMessageVO.getMsgState());
        globalMessage.setMessageModule(wsMessageVO.getMsgModule());
        globalMessageMapper.insert(globalMessage);
    }
}
