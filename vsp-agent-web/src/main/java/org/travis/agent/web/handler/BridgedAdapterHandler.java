package org.travis.agent.web.handler;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.travis.api.client.center.CenterHostClient;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.agent.web.config.StartDependentConfig;
import org.travis.shared.common.constants.AgentDependentConstant;
import org.travis.shared.common.constants.NetworkLayerConstant;
import org.travis.shared.common.domain.R;
import oshi.hardware.NetworkIF;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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
    public CenterHostClient centerHostClient;
    @Resource
    private StartDependentConfig startDependentConfig;

    public void execBridgedAdapter(HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO) {
        // 1.查询网卡列表
        List<NetworkIF> networkInterfaces = OshiUtil.getNetworkIFs();
        Map<String, NetworkIF> networkInterfaceMap = networkInterfaces.stream().collect(Collectors.toMap(NetworkIF::getName, one -> one));

        // 2.获取目标网卡名称
        String targetInterfaceName = NetworkLayerConstant.INTERFACE_BR_NAME_PREFIX + hostBridgedAdapterToAgentDTO.getNicName().trim();

        /*
           准备桥接网卡
         */
        boolean isSuccess;
        String stateMessage;

        if (networkInterfaceMap.containsKey(targetInterfaceName)) {
            // 3.如果目标网卡存在
            NetworkIF networkInterface = networkInterfaceMap.get(targetInterfaceName);

            if (!networkInterface.isConnectorPresent()) {
                // 3.1.目标网卡存在但未启用
                isSuccess = false;
                stateMessage = "目标网卡存在但未启用，请使用 `nmcli con up {目标网卡:br0-vsp-xxx}` 命令手动启用目标网卡!";
            } else {
                // 3.2.目标网卡存在并启用
                isSuccess = true;
                stateMessage = "桥接网卡就绪-已存在";
            }

        } else {
            // 4.目标网卡不存在
            if (networkInterfaceMap.containsKey(hostBridgedAdapterToAgentDTO.getNicName())) {
                // 4.1.源网卡存在
                NetworkIF networkInterface = networkInterfaceMap.get(hostBridgedAdapterToAgentDTO.getNicName());

                if (!networkInterface.isConnectorPresent()) {
                    // 4.1.1.源网卡存在但是未启用
                    isSuccess = false;
                    stateMessage = "源网卡存在但未启用, 请手动启用源网卡并设置静态 IP 等信息!";
                } else {
                    // 4.1.2.源网卡存在并且已启用 -> 执行创建网桥命令
                    // 创建网桥
                    try {
                        RuntimeUtil.execForStr("/bin/sh " + startDependentConfig.getFilePrefix() + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_BRIDGE_KEY));
                        isSuccess = true;
                        stateMessage = "桥接网卡就绪-创建成功";
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        isSuccess = false;
                        stateMessage = "桥接网卡创建失败!";
                    }
                }
            } else {
                // 4.2.源网卡不存在
                isSuccess = false;
                stateMessage = "源网卡不存在!";
            }
        }

        // 5.如果桥接网卡创建成功，继续创建虚拟网络
        if (isSuccess) {
            try {
                RuntimeUtil.execForStr("/bin/sh " + startDependentConfig.getFilePrefix() + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_VIRSH_NETWORK_KEY));
                stateMessage = stateMessage + " | 虚拟网络就绪-创建成功";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                isSuccess = false;
                stateMessage = stateMessage + " | 虚拟网络创建失败";
            }
        }

        // 6.执行结果回调
        R<Void> sendBridgedInitMessageR = centerHostClient.sendBridgedInitMessage(hostBridgedAdapterToAgentDTO.getId(), isSuccess, stateMessage);
        if (sendBridgedInitMessageR != null && sendBridgedInitMessageR.checkFail()) {
            log.error("[BridgedAdapterHandler::execBridgedAdapter] Failed to send Bridged Init Message: {}", sendBridgedInitMessageR.getMsg());
        }
    }
}
