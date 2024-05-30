package org.travis.host.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.shared.common.constants.VmwareConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName AgentVmwareClientImpl
 * @Description AgentVmwareClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Slf4j
@DubboService
public class AgentVmwareClientImpl implements AgentVmwareClient {
    @Override
    public R<Void> createVmware(String targetAgentIp, String xmlContent, Long vmwareId) {
        try {
            // TODO 测试命令执行
            // 1.将字符串写入临时 xml 文件中
            String tmpPath = VmwareConstant.TMP_XML_FOLDER + File.separator + vmwareId + ".xml";
            FileUtil.writeString(xmlContent, tmpPath, StandardCharsets.UTF_8);
            // 2.执行虚拟机定义
            RuntimeUtil.execForStr("virsh define " + tmpPath);
            return R.ok();
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::createVmware] Agent Vmware Create Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> startVmware(String targetAgentIp, String vmwareUuid) {
        try {
            // TODO 测试命令执行
            String execked = RuntimeUtil.execForStr("virsh start " + vmwareUuid);
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::startVmware] Agent Vmware Start Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> suspendVmware(String targetAgentIp, String vmwareUuid) {
        try {
            // TODO 测试命令执行
            String execked = RuntimeUtil.execForStr("virsh suspend " + vmwareUuid);
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::suspendVmware] Agent Vmware Suspend Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> complexVmware(String targetAgentIp, String vmwareUuid, String subParam) {
        return null;
    }

    @Override
    public R<String> resumeVmware(String hostIp, String vmwareUuid) {
        try {
            // TODO 测试命令执行
            String execked = RuntimeUtil.execForStr("virsh resume " + vmwareUuid);
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::resumeVmware] Agent Vmware Resume Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> shutdownVmware(String hostIp, String vmwareUuid) {
        try {
            // TODO 测试命令执行
            String execked = RuntimeUtil.execForStr("virsh shutdown " + vmwareUuid);
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::shutdownVmware] Agent Vmware Shutdown Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> destroyVmware(String hostIp, String vmwareUuid) {
        try {
            // TODO 测试命令执行
            String execked = RuntimeUtil.execForStr("virsh destroy " + vmwareUuid);
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::destroyVmware] Agent Vmware Destroy Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
