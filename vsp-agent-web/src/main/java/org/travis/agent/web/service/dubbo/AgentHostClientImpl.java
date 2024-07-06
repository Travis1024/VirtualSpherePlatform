package org.travis.agent.web.service.dubbo;

import cn.hutool.json.JSONUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.agent.web.pojo.bo.BridgeInitResultMessageBO;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.api.client.center.CenterMessageClient;
import org.travis.api.pojo.bo.HostDetailsBO;
import org.travis.api.pojo.bo.HostResourceInfoBO;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.agent.web.config.StartDependentConfig;
import org.travis.agent.web.handler.BridgedAdapterHandler;
import org.travis.shared.common.constants.AgentDependentConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;
import oshi.hardware.VirtualMemory;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @ClassName AgentHostClientImpl
 * @Description AgentHostClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Slf4j
@DubboService
public class AgentHostClientImpl implements AgentHostClient {
    @Resource
    public BridgedAdapterHandler bridgedAdapterHandler;
    @Resource
    public StartDependentConfig startDependentConfig;
    @DubboReference
    public CenterMessageClient centerMessageClient;

    @Override
    public R<HostDetailsBO> queryHostInfoDetails(String targetAgentIp) {
        try {
            // 1.查询主机架构信息
            String osArch = System.getProperty("os.arch");
            // 2.查询主机内存大小（字节）
            long memoryTotal = OshiUtil.getMemory().getTotal();
            // 3.查询主机 CPU 核数
            Integer cpuNum = OshiUtil.getCpuInfo().getCpuNum();

            // 4.查询主机总虚拟核数、已使用虚拟核数
            List<String> execkedForLineList = execQueryCpuNumberInfo();
            int vCpuActiveNum = Integer.parseInt(execkedForLineList.get(0));
            int vCpuDefinitionNum = Integer.parseInt(execkedForLineList.get(1));
            int vCpuAllNum = Integer.parseInt(execkedForLineList.get(2));

            // 5.封装响应信息
            HostDetailsBO hostDetailsBO = new HostDetailsBO();
            hostDetailsBO.setOsArch(osArch);
            hostDetailsBO.setMemoryTotal(memoryTotal);
            hostDetailsBO.setCpuNum(cpuNum);
            hostDetailsBO.setVCpuActiveNum(vCpuActiveNum);
            hostDetailsBO.setVCpuDefinitionNum(vCpuDefinitionNum);
            hostDetailsBO.setVCpuAllNum(vCpuAllNum);

            return R.ok(hostDetailsBO);
        } catch (Exception e) {
            log.error("[AgentHostClientImpl::queryHostInfoDetails] Agent Host Query Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    private List<String> execQueryCpuNumberInfo() {
        R<List<String>> listR = VspRuntimeUtil.execForLines("/bin/sh " + startDependentConfig.getFilePrefix() + File.separator + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_VIRSH_CPU_NUMBER_KEY));
        if (listR.checkFail()) {
            throw new DubboFunctionException("虚拟核数 Shell 脚本查询任务执行失败! -> " + listR.getMsg());
        }
        List<String> execkedForLineList = listR.getData();
        if (execkedForLineList == null || execkedForLineList.size() != 3) {
            throw new DubboFunctionException("虚拟核数 Shell 脚本查询任务执行失败! -> " + JSONUtil.toJsonStr(execkedForLineList));
        }
        return execkedForLineList;
    }

    @Override
    public void execBridgedAdapter(String targetAgentIp, HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO) {
        BridgeInitResultMessageBO bridgeInitResultMessageBO = null;
        try {
            // 1.执行网卡桥接命令
            bridgeInitResultMessageBO = bridgedAdapterHandler.execBridgedAdapter(hostBridgedAdapterToAgentDTO);
            // 2.执行回调
            centerMessageClient.sendBridgedInitResultMessage(bridgeInitResultMessageBO.getHostId(), hostBridgedAdapterToAgentDTO.getHostName(), bridgeInitResultMessageBO.getIsSuccess(), bridgeInitResultMessageBO.getStateMessage());
        } catch (Exception e) {
            log.error("[AgentHostClientImpl::execBridgedAdapter] Agent Exec Bridged Adapter Error! -> {}", e.getMessage());
            // 执行回调
            centerMessageClient.sendBridgedInitResultMessage(hostBridgedAdapterToAgentDTO.getHostId(), hostBridgedAdapterToAgentDTO.getHostName(), false, (bridgeInitResultMessageBO != null ? bridgeInitResultMessageBO.getStateMessage() : null) + e.getMessage());
        }
    }

    @Override
    public R<HostResourceInfoBO> queryHostResourceInfo(String targetAgentIp) {
        try {
            // 1.查询内存相关信息
            VirtualMemory virtualMemory = OshiUtil.getMemory().getVirtualMemory();
            long memoryMax = virtualMemory.getVirtualMax();
            long memoryInUse = virtualMemory.getVirtualInUse();

            // 2.查询主机总虚拟核数、已使用虚拟核数
            List<String> execkedForLineList = execQueryCpuNumberInfo();
            int vCpuActiveNum = Integer.parseInt(execkedForLineList.get(0));
            int vCpuDefinitionNum = Integer.parseInt(execkedForLineList.get(1));
            int vCpuAllNum = Integer.parseInt(execkedForLineList.get(2));

            // 3.封装响应信息
            HostResourceInfoBO hostResourceInfoBO = new HostResourceInfoBO();
            hostResourceInfoBO.setMemoryTotalMax(memoryMax);
            hostResourceInfoBO.setMemoryTotalInUse(memoryInUse);
            hostResourceInfoBO.setVCpuAllNum(vCpuAllNum);
            hostResourceInfoBO.setVCpuActiveNum(vCpuActiveNum);
            hostResourceInfoBO.setVCpuDefinitionNum(vCpuDefinitionNum);

            return R.ok(hostResourceInfoBO);

        } catch (Exception e) {
            log.error("[AgentHostClientImpl::queryHostResourceInfo] Query Host Resource Info Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
