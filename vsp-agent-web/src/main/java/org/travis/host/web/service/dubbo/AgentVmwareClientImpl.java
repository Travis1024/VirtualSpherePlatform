package org.travis.host.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.shared.common.constants.VmwareConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;

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
            // 1.将字符串写入临时 xml 文件中
            String tmpPath = VmwareConstant.TMP_XML_FOLDER + File.separator + vmwareId + ".xml";
            FileUtil.writeString(xmlContent, tmpPath, StandardCharsets.UTF_8);
            // 2.执行虚拟机定义
            String execked = RuntimeUtil.execForStr("virsh define " + tmpPath);
            Assert.isTrue(execked.contains("defined from"), () -> new DubboFunctionException(StrUtil.format("虚拟机创建失败:{}", execked)));
            return R.ok();
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::createVmware] Agent Vmware Create Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> startVmware(String targetAgentIp, String vmwareUuid) {
        try {
            String execked = RuntimeUtil.execForStr("virsh start " + vmwareUuid);
            Assert.isTrue(execked.contains("started"), () -> new DubboFunctionException(StrUtil.format("虚拟机启动失败:{}", execked)));
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::startVmware] Agent Vmware Start Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> suspendVmware(String targetAgentIp, String vmwareUuid) {
        try {
            String execked = RuntimeUtil.execForStr("virsh suspend " + vmwareUuid);
            Assert.isTrue(execked.contains("suspended"), () -> new DubboFunctionException(StrUtil.format("虚拟机暂停失败:{}", execked)));
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
    public R<String> resumeVmware(String targetAgentIp, String vmwareUuid) {
        try {
            String execked = RuntimeUtil.execForStr("virsh resume " + vmwareUuid);
            Assert.isTrue(execked.contains("resumed"), () -> new DubboFunctionException(StrUtil.format("虚拟机恢复失败:{}", execked)));
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::resumeVmware] Agent Vmware Resume Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> shutdownVmware(String targetAgentIp, String vmwareUuid) {
        try {
            String execked = RuntimeUtil.execForStr("virsh shutdown " + vmwareUuid);
            Assert.isTrue(execked.contains("is being shutdown"), () -> new DubboFunctionException(StrUtil.format("虚拟机关机失败:{}", execked)));
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::shutdownVmware] Agent Vmware Shutdown Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> destroyVmware(String targetAgentIp, String vmwareUuid) {
        try {
            String execked = RuntimeUtil.execForStr("virsh destroy " + vmwareUuid);
            Assert.isTrue(execked.contains("destroyed"), () -> new DubboFunctionException(StrUtil.format("虚拟机强制关机失败:{}", execked)));
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::destroyVmware] Agent Vmware Destroy Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> undefineVmware(String targetAgentIp, String vmwareUuid) {
        try {
            String execked = RuntimeUtil.execForStr("virsh undefine " + vmwareUuid);
            Assert.isTrue(execked.contains("has been undefined"), () -> new DubboFunctionException(StrUtil.format("虚拟机删除失败:{}", execked)));
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::undefineVmware] Agent Vmware Undefine Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
