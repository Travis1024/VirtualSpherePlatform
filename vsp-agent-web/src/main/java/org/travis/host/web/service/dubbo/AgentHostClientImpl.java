package org.travis.host.web.service.dubbo;

import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.api.pojo.bo.HostDetailsBO;
import org.travis.api.pojo.dto.HostBridgedAdapterToAgentDTO;
import org.travis.host.web.handler.BridgedAdapterHandler;
import org.travis.host.web.utils.AgentThreadPoolConfig;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

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
    private BridgedAdapterHandler bridgedAdapterHandler;

    @Override
    public R<HostDetailsBO> queryHostInfoDetails(String targetAgentIp) {
        try {
            // 1.查询主机架构信息
            String osArch = System.getProperty("os.arch");
            // 2.查询主机内存大小（字节）
            long memoryTotal = OshiUtil.getMemory().getTotal();
            // 3.查询主机 CPU 核数
            Integer cpuNum = OshiUtil.getCpuInfo().getCpuNum();

            HostDetailsBO hostDetailsBO = new HostDetailsBO();
            hostDetailsBO.setOsArch(osArch);
            hostDetailsBO.setMemoryTotal(memoryTotal);
            hostDetailsBO.setCpuNum(cpuNum);

            return R.ok(hostDetailsBO);
        } catch (Exception e) {
            log.error("[AgentHostClientImpl::queryHostInfoDetails] Agent Host Query Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> execBridgedAdapter(String targetAgentIp, HostBridgedAdapterToAgentDTO hostBridgedAdapterToAgentDTO) {
        try {
            // TODO 测试命令执行
            // 异步执行网卡桥接命令
            CompletableFuture.runAsync(() -> bridgedAdapterHandler.execBridgedAdapter(hostBridgedAdapterToAgentDTO), AgentThreadPoolConfig.singleExecutor);
            return R.ok("桥接网卡及虚拟网络初始化中···");
        } catch (Exception e) {
            log.error("[AgentHostClientImpl::execBridgedAdapter] Agent Exec Bridged Adapter Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
