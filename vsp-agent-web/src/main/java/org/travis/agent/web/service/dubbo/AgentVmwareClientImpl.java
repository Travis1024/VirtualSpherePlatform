package org.travis.agent.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.constants.VmwareConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;

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
            FileUtil.mkParentDirs(tmpPath);
            FileUtil.writeString(xmlContent, tmpPath, StandardCharsets.UTF_8);
            // 2.执行虚拟机定义
            R<String> stringR = VspRuntimeUtil.execForStr("virsh define " + tmpPath);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机创建失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
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
            R<String> stringR = VspRuntimeUtil.execForStr("virsh start " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机启动失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
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
            R<String> stringR = VspRuntimeUtil.execForStr("virsh suspend " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机暂停失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
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
            R<String> stringR = VspRuntimeUtil.execForStr("virsh resume " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机恢复失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
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
            // 1.执行正常关机操作
            R<String> stringR = VspRuntimeUtil.execForStr("virsh shutdown " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机关机失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
            Assert.isTrue(execked.contains("is being shutdown"), () -> new DubboFunctionException(StrUtil.format("虚拟机关机失败:{}", execked)));
            long beginTimeMillis = System.currentTimeMillis();

            // 2.循环等待关机
            while (true) {
                Thread.sleep(2000);
                // 2.1.判断是否超时，超时执行强制关机
                if (System.currentTimeMillis() - beginTimeMillis > VmwareConstant.VMWARE_SHUTDOWN_TIMEOUT) {
                    log.warn("[AgentVmwareClientImpl::shutdownVmware] 正常关机超时, 尝试强制关机!");
                    return destroyVmware(targetAgentIp, vmwareUuid);
                }
                // 2.2.判断虚拟机状态
                R<String> stringR1 = VspRuntimeUtil.execForStr("virsh domstate " + vmwareUuid);
                Assert.isTrue(stringR1.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机状态查询失败:{}", stringR1.getMsg())));
                String vmwareState = stringR1.getData();
                if (vmwareState.contains("shut off")) {
                    break;
                }
            }
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::shutdownVmware] Agent Vmware Shutdown Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> destroyVmware(String targetAgentIp, String vmwareUuid) {
        try {
            R<String> stringR = VspRuntimeUtil.execForStr("virsh destroy " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机强制关机失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
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
            R<String> stringR = VspRuntimeUtil.execForStr("virsh undefine " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException(StrUtil.format("虚拟机删除失败:{}", stringR.getMsg())));
            String execked = stringR.getData();
            Assert.isTrue(execked.contains("has been undefined"), () -> new DubboFunctionException(StrUtil.format("虚拟机删除失败:{}", execked)));
            return R.ok(execked);
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::undefineVmware] Agent Vmware Undefine Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> modifyVmwareMemory(String targetAgentIp, String vmwareUuid, Long memory, boolean isPowerOff) {
        try {
            // 非关机状态-内存临时生效
            if (!isPowerOff) {
                R<String> stringR = VspRuntimeUtil.execForStr("virsh setmem " + vmwareUuid + " " + memory / SystemConstant.KB_UNIT);
                Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("虚拟机 live 内存修改失败:" + stringR.getMsg()));
                String execked = stringR.getData();
                Assert.isFalse(execked.contains("error"), () -> new DubboFunctionException("live:" + execked));
            }
            // 虚拟内存下次启动生效并持久化
            R<String> stringR = VspRuntimeUtil.execForStr("virsh setmem " + vmwareUuid + " " + memory / SystemConstant.KB_UNIT + " --config");
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("虚拟机 config 内存修改失败:" + stringR.getMsg()));
            String execked = stringR.getData();
            Assert.isTrue(StrUtil.isEmpty(execked.trim()), () -> new DubboFunctionException("config:" + execked));
            return R.ok();
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::modifyVmwareMemory] Agent Vmware Memory Size Modify Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> modifyVmwareVcpu(String targetAgentIp, String vmwareUuid, Integer vcpuNumber, boolean isPowerOff) {
        try {
            // 非关机状态-虚拟CPU临时生效
            if (!isPowerOff) {
                R<String> stringR = VspRuntimeUtil.execForStr("virsh setvcpus " + vmwareUuid + " " + vcpuNumber + " --live");
                Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("虚拟机 live vcpu 修改失败:" + stringR.getMsg()));
                String execked = stringR.getData();
                Assert.isFalse(execked.contains("error"), () -> new DubboFunctionException("live:" + execked));
            }
            // 虚拟CPU下次启动生效并持久化
            R<String> stringR = VspRuntimeUtil.execForStr("virsh setvcpus " + vmwareUuid + " " + vcpuNumber + " --config");
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("虚拟机 config vcpu 修改失败:" + stringR.getMsg()));
            String execked = stringR.getData();
            Assert.isTrue(StrUtil.isEmpty(execked.trim()), () -> new DubboFunctionException("config:" + execked));
            return R.ok();
        } catch (Exception e) {
            log.error("[AgentVmwareClientImpl::modifyVmwareVcpu] Agent Vmware Vcpu Number Modify Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> queryVncAddress(String targetAgentIp, String vmwareUuid) {
        try {
            // 1.查询 VNC 端口
            R<String> stringR = VspRuntimeUtil.execForStr("virsh vncdisplay " + vmwareUuid);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("VNC查询失败:" + stringR.getMsg()));
            String execked = stringR.getData().trim();
            Assert.isTrue(execked.startsWith(StrUtil.COLON), () -> new DubboFunctionException("VNC查询失败:" + execked));
            // 2.组装真实端口
            String portStr = execked.substring(1);
            int realPort = 5900 + Integer.parseInt(portStr);
            // 192.168.0.201:5900
            return R.ok(targetAgentIp + StrUtil.COLON + realPort);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentVmwareClientImpl::queryVncAddress] Agent Vmware Query Vnc Address Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
