package org.travis.agent.web.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.travis.agent.web.pojo.bo.BridgeInitResultMessageBO;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.agent.web.config.StartDependentConfig;
import org.travis.shared.common.constants.AgentDependentConstant;
import org.travis.shared.common.constants.NetworkLayerConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;
import oshi.hardware.NetworkIF;

import javax.annotation.Resource;
import java.io.File;
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

    @Resource
    public StartDependentConfig startDependentConfig;

    public BridgeInitResultMessageBO execBridgedAdapter(HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO) {
        // 1.查询网卡列表
        log.debug("1.查询网卡列表");
        List<NetworkIF> networkInterfaces = OshiUtil.getNetworkIFs();
        Map<String, NetworkIF> networkInterfaceMap = networkInterfaces.stream().collect(Collectors.toMap(NetworkIF::getName, one -> one));

        // 2.获取目标网卡名称
        log.debug("2.获取目标网卡名称");
        String targetInterfaceName = NetworkLayerConstant.INTERFACE_BR_NAME_PREFIX + hostBridgedAdapterToAgentDTO.getNicName().trim();

        /*
           准备桥接网卡
         */
        log.debug("3|4.准备桥接网卡");
        boolean isSuccess;
        String stateMessage;

        if (networkInterfaceMap.containsKey(targetInterfaceName)) {
            // 3.如果目标网卡存在
            NetworkIF networkInterface = networkInterfaceMap.get(targetInterfaceName);

            if (!networkInterface.isConnectorPresent()) {
                // 3.1.目标网卡存在但未启用
                isSuccess = false;
                stateMessage = "目标网卡存在但未启用，请使用 `nmcli connection up {目标网卡:br0-vsp-xxx}` 命令手动启用目标网卡!";
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
                        // eg:/bin/sh /opt/vsp/dependent/init_bridge.sh br0-vsp-p4p1 p4p1
                        R<String> stringR = VspRuntimeUtil.execForStr(
                                "/bin/sh" + StrUtil.SPACE +
                                        startDependentConfig.getFilePrefix() + File.separator + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_BRIDGE_KEY) + StrUtil.SPACE +
                                        targetInterfaceName + StrUtil.SPACE +
                                        hostBridgedAdapterToAgentDTO.getNicName()
                        );
                        Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("网桥创建失败:" + stringR.getMsg()));
                        String execked = stringR.getData();
                        Assert.isTrue(execked.contains("Bridge setup script completed successfully."), () -> new DubboFunctionException(execked));
                        isSuccess = true;
                        stateMessage = "桥接网卡就绪-创建成功";
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        isSuccess = false;
                        stateMessage = "桥接网卡创建失败! " + e.getMessage();
                    }
                }
            } else {
                // 4.2.源网卡不存在
                isSuccess = false;
                stateMessage = "源网卡不存在!";
            }
        }

        // 5.如果桥接网卡创建成功，继续创建虚拟网络
        log.debug("5.桥接网卡创建成功,继续创建虚拟网络");
        if (isSuccess) {
            try {
                R<String> stringR = VspRuntimeUtil.execForStr("/bin/sh " + startDependentConfig.getFilePrefix() + File.separator + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_VIRSH_NETWORK_KEY));
                Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("虚拟网络创建失败:" + stringR.getMsg()));
                stateMessage = stateMessage + " | 虚拟网络就绪-创建成功";
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                isSuccess = false;
                stateMessage = stateMessage + " | 虚拟网络创建失败";
            }
        }

        // 6.返回执行结果
        log.debug("6.返回执行结果");
        BridgeInitResultMessageBO bridgeInitResultMessageBO = new BridgeInitResultMessageBO();
        bridgeInitResultMessageBO.setHostId(hostBridgedAdapterToAgentDTO.getHostId());
        bridgeInitResultMessageBO.setIsSuccess(isSuccess);
        bridgeInitResultMessageBO.setStateMessage(stateMessage);
        log.debug(JSONUtil.toJsonStr(bridgeInitResultMessageBO));
        return bridgeInitResultMessageBO;
    }
}
