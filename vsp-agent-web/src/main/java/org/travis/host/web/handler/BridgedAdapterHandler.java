package org.travis.host.web.handler;

import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.travis.api.client.center.CenterHostClient;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.shared.common.constants.NetworkLayerConstant;
import org.travis.shared.common.domain.R;
import oshi.hardware.NetworkIF;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName BridgedAdapterHandler
 * @Description BridgedAdapterHandler
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Slf4j
@Component
public class BridgedAdapterHandler {

    @DubboReference
    private CenterHostClient centerHostClient;

    public void execBridgedAdapter(HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO) {
        // 查询网卡列表
        List<NetworkIF> networkInterfaces = OshiUtil.getNetworkIFs();
        Set<String> networkInterfaceNameSet = networkInterfaces.stream().map(one -> one.getName().trim()).collect(Collectors.toSet());

        // 获取目标网卡名称
        String targetInterfaceName = NetworkLayerConstant.INTERFACE_BR_NAME_PREFIX + hostBridgedAdapterToAgentDTO.getNicName().trim();

        // 如果目标网卡存在
        if (networkInterfaceNameSet.contains(targetInterfaceName)) {
            R<Void> sendBridgedInitMessageR = centerHostClient.sendBridgedInitMessage(hostBridgedAdapterToAgentDTO.getId(), true, "桥接网卡就绪-已存在");
            if (sendBridgedInitMessageR.checkFail()) {
                log.error("[BridgedAdapterHandler::execBridgedAdapter] Failed to send Bridged Init Message: {}", sendBridgedInitMessageR.getMsg());
            }
            return;
        }

        for (NetworkIF networkInterface : networkInterfaces) {
            // 1.如果未匹配到网卡名称则跳过
            if (!networkInterface.getName().equals(hostBridgedAdapterToAgentDTO.getNicName())) {
                continue;
            }

        }
    }
}
