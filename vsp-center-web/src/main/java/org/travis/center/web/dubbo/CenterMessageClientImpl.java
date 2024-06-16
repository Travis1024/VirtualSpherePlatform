package org.travis.center.web.dubbo;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.center.CenterMessageClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.enums.HostStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.support.websocket.WsMessageHolder;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.enums.MsgModuleEnum;
import org.travis.shared.common.enums.MsgStateEnum;
import org.travis.shared.common.exceptions.BadRequestException;

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
    public WsMessageHolder wsMessageHolder;
    @Resource
    public HostInfoMapper hostInfoMapper;

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
    public void sendBridgedInitResultMessage(Long hostId, String hostName, boolean isSuccess, String stateMessage) {
        try {
            int updated = hostInfoMapper.update(
                    Wrappers.<HostInfo>lambdaUpdate()
                            .eq(HostInfo::getId, hostId)
                            .set(HostInfo::getStateMessage, stateMessage)
                            .set(HostInfo::getState, isSuccess ? HostStateEnum.READY : HostStateEnum.ERROR)
            );
            Assert.isTrue(updated != 0, () -> new BadRequestException("宿主机状态更新失败, 未找到当前宿主机信息!"));

            wsMessageHolder.sendGlobalMessage(
                    WebSocketMessage.builder()
                            .msgTitle("宿主机创建")
                            .msgModule(MsgModuleEnum.HOST)
                            .msgState(MsgStateEnum.INFO)
                            .msgContent(hostName + " -> " + "宿主机创建成功!")
                            .build()
            );

        } catch (Exception e) {
            log.error("[CenterHostClientImpl::sendBridgedInitMessage] Modify Host Bridged Init State Error!");
            wsMessageHolder.sendGlobalMessage(
                    WebSocketMessage.builder()
                            .msgTitle("宿主机创建")
                            .msgModule(MsgModuleEnum.HOST)
                            .msgState(MsgStateEnum.ERROR)
                            .msgContent(hostName + " -> 宿主机创建失败:" + e.getMessage())
                            .build()
            );
        }
    }
}
