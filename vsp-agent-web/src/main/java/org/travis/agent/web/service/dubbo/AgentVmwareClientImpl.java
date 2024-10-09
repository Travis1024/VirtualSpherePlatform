package org.travis.agent.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.constants.VmwareConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.travis.shared.common.utils.VspRuntimeUtil.mapToStringArray;

/**
 * @ClassName AgentVmwareClientImpl
 * @Description AgentVmwareClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Slf4j
@Component
@DubboService(
        methods = {
                // 热迁移超时时间: 10 分钟
                @Method(name = "liveMigrate", timeout = 600000),
                // 冷迁移超时时间: 10 分钟
                @Method(name = "offlineMigrate", timeout = 600000)
        }
)
public class AgentVmwareClientImpl implements AgentVmwareClient {

    @Resource
    private RedissonClient redissonClient;

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

    @Override
    public R<String> queryIpAddress(String targetAgentIp, String vmwareUuid, String netRange) {
        try {
            Map<String, String> macIpMap = new HashMap<>();

            // 1.NMAP 扫描网段内 IP 地址
            R<List<String>> listR = VspRuntimeUtil.execForLines("nmap -sP " + netRange);
            Assert.isTrue(listR.checkSuccess(), () -> new DubboFunctionException("NMAP-扫描网段失败:" + listR.getMsg()));
            List<String> data = listR.getData();
            String mac = null;
            String ip = null;
            for (String oneLine : data) {
                oneLine = oneLine.trim();
                if (oneLine.startsWith("Nmap scan report for")) {
                    String[] split = oneLine.split("\\s+");
                    ip = split[4];
                } else if (oneLine.startsWith("MAC Address:")) {
                    String[] split = oneLine.split("\\s+");
                    mac = split[2];
                    macIpMap.put(mac, ip);
                }
            }

            // 2.查询虚拟机 MAC 地址
            R<List<String>> macListR = VspRuntimeUtil.execForLines("virsh domiflist " + vmwareUuid);
            Assert.isTrue(macListR.checkSuccess(), () -> new DubboFunctionException("虚拟机 MAC 查询失败:" + macListR.getMsg()));
            List<String> macList = macListR.getData();
            String macAddr = null;
            for (int i = 2; i < macList.size(); i++) {
                String line = macList.get(i).trim();
                String[] split = line.split("\\s+");
                if (macIpMap.containsKey(split[split.length - 1].toUpperCase())) {
                    macAddr = split[split.length - 1].toUpperCase();
                    break;
                }
            }

            if (StrUtil.isEmpty(macAddr)) {
                return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), "虚拟机 MAC 地址未找到");
            }

            return R.ok(macIpMap.get(macAddr));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentVmwareClientImpl::queryIpAddress] Agent Vmware Query Ip Address Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> diskUnmount(String targetAgentIp, String vmwareUuid, Set<String> targetDevSet) {
        try {
            for (String targetDev : targetDevSet) {
                log.info("[AgentVmwareClientImpl::diskUnmount] Disk Unmount Target Dev -> {}", targetDev);
                R<String> execked = VspRuntimeUtil.execForStr("virt-xml " + vmwareUuid + " --remove-device --disk target=" + targetDev);
                log.info("Disk Unmount Result -> {}", JSONUtil.toJsonStr(execked));
                Assert.isTrue(execked.checkSuccess(), () -> new DubboFunctionException("虚拟机磁盘卸载失败:" + execked.getMsg()));
                String result = execked.getData().trim();
                Assert.isTrue(result.contains("defined successfully"), () -> new DubboFunctionException("虚拟机磁盘卸载失败:" + result));
            }
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentVmwareClientImpl::diskUnmount] Disk Unmount Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> diskMount(String targetAgentIp, String vmwareUuid, String hostSharedStoragePath, Set<String> diskPathSet) {
        try {
            for (String subPath : diskPathSet) {
                String absolutePath = hostSharedStoragePath + subPath;
                log.info("[AgentVmwareClientImpl::diskMount] Disk Mount Path -> {}", absolutePath);
                R<String> execked = VspRuntimeUtil.execForStr("virt-xml " + vmwareUuid + " --add-device --disk " + absolutePath + ",format=qcow2,bus=virtio");
                log.info("Disk Mount Result -> {}", JSONUtil.toJsonStr(execked));
                Assert.isTrue(execked.checkSuccess(), () -> new DubboFunctionException("虚拟机磁盘挂载失败:" + execked.getMsg()));
                String result = execked.getData().trim();
                Assert.isTrue(result.contains("defined successfully"), () -> new DubboFunctionException("虚拟机磁盘挂载失败:" + result));
            }
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentVmwareClientImpl::diskMount] Disk Mount Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> liveMigrate(String targetAgentIp, String targetHostIp, String targetHostLoginPassword, String vmwareUuid) {
        try {
            String command = StrUtil.format("sshpass -p {} virsh migrate {} qemu+ssh://{}/system tcp://{} --live --persistent --verbose --unsafe", targetHostLoginPassword, vmwareUuid, targetHostIp, targetHostIp);
            log.info("[AgentVmwareClientImpl::liveMigrate] Live Migrate Command -> {}", command);
            return processRealTimeMigrateInfo(vmwareUuid, command);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentVmwareClientImpl::liveMigrate] Live Migrate Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> offlineMigrate(String targetAgentIp, String targetHostIp, String targetHostLoginPassword, String vmwareUuid) {
        try {
            String command = StrUtil.format("sshpass -p {} virsh migrate {} qemu+ssh://{}/system tcp://{} --offline --undefinesource --persistent --verbose --unsafe", targetHostLoginPassword, vmwareUuid, targetHostIp, targetHostIp);
            log.info("[AgentVmwareClientImpl::offlineMigrate] Offline Migrate Command -> {}", command);
            return processRealTimeMigrateInfo(vmwareUuid, command);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentVmwareClientImpl::offlineMigrate] Offline Migrate Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    private R<Void> processRealTimeMigrateInfo(String vmwareUuid, String... commands) {
        Map<String, String> newEnvMap = new HashMap<>(System.getenv());
        newEnvMap.put("LANG", "en_US.UTF-8");
        Process process = RuntimeUtil.exec(mapToStringArray(newEnvMap), commands);
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = null;

        // 初始化 redis 进度数据
        RBucket<Integer> bucket = redissonClient.getBucket(RedissonConstant.VMWARE_MIGRATE_PROGRESS_PREFIX + vmwareUuid);
        bucket.set(0);

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "", tmp = "";

            while ((line = bufferedReader.readLine()) != null) {
                log.info("{} Migrate Info -> {}", vmwareUuid, line);
                List<String> list = ReUtil.findAll("\\[\\s*(\\d+)\\s*%\\s*\\]", line, 1);
                if (list.isEmpty()) {
                    continue;
                }
                if (!tmp.equals(list.get(0))) {
                    bucket.set(Integer.parseInt(list.get(0)));
                    tmp = list.get(0);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.error(BizCodeEnum.INTERNAL_MESSAGE.getCode(), e.getMessage());
        }

        // 10s 后释放进度数据
        bucket.expire(Instant.now().plus(10, ChronoUnit.SECONDS));

        int exitValue = process.exitValue();
        if (exitValue == 0) {
            log.info("[processRealTimeMigrateInfo] ExitValue: {}", exitValue);
        } else {
            log.error("[processRealTimeMigrateInfo] ExitValue: {}", exitValue);
        }
        return exitValue == 0 ? R.ok() : R.error(BizCodeEnum.INTERNAL_MESSAGE.getCode(), RuntimeUtil.getErrorResult(process, CharsetUtil.CHARSET_UTF_8));
    }
}
