package org.travis.center.message.websocket.service;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.travis.center.common.utils.MessageThreadPoolConfig;
import org.travis.center.message.pojo.vo.WsMessageVO;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName WebSocketGlobalMsg
 * @Description 全局消息推送
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws/global/{random}")
public class WsGlobalMsg {

    /**
     * 全局 Session 连接池
     */
    public static ConcurrentHashSet<Session> sessionPool = new ConcurrentHashSet<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("random") String random) {
        try {
            sessionPool.add(session);
            log.info("[WebSocketGlobalMsg::onOpen] -> {} 连接成功!", session.getId());
        } catch (Exception e) {
            log.error("[WebSocketGlobalMsg::onOpen] 连接失败!");
            log.error(e.getMessage(), e);
            try {
                sessionPool.remove(session);
                if (session.isOpen()) {
                    session.close();
                }
            } catch (IOException ioException) {
                log.error(ioException.getMessage(), ioException);
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessionPool.remove(session);
        log.info("[WebSocketGlobalMsg::onClose] -> {} 断开连接! CloseReason:{}", session.getId(), closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("[WebSocketGlobalMsg::onMessage] -> {}", message);
    }

    /**
     * 推送全局消息（双重锁检查）
     *
     * @param message json 消息
     */
    public static void sendGlobalMsg(String message) {
        sessionPool.iterator().forEachRemaining(session -> {
            if (session.isOpen()) {
                CompletableFuture.runAsync(() -> {
                    synchronized (session) {
                        if (session.isOpen()) {
                            try {
                                session.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
                            } catch (Exception e) {
                                log.error("[WebSocketGlobalMsg::sendGlobalMsg] -> {} send global message error!", session.getId());
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }, MessageThreadPoolConfig.messageProcessExecutor);
            }
        });
    }
}
