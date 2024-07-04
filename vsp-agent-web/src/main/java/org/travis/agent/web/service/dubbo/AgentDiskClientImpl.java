package org.travis.agent.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.stereotype.Component;
import org.travis.api.client.agent.AgentDiskClient;
import org.travis.agent.web.config.StartDependentConfig;
import org.travis.shared.common.constants.AgentDependentConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.VspRuntimeUtil;

import javax.annotation.Resource;
import java.io.File;

/**
 * @ClassName AgentDiskClientImpl
 * @Description AgentDiskClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/23
 */
@Slf4j
@DubboService(
        methods = {
                // 磁盘复制超时时间: 2 小时
                @Method(name = "copyDiskFile", timeout = 7200000)
        }
)
public class AgentDiskClientImpl implements AgentDiskClient {
    @Resource
    public StartDependentConfig startDependentConfig;

    @Override
    public R<String> createDisk(String targetAgentIp, String path, Long unitGbSize) {
        try {
            checkFileSuffix(path);
            R<String> stringR = VspRuntimeUtil.execForStr("qemu-img create -f qcow2 -o preallocation=off " + path + " " + unitGbSize.toString() + "G");
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("磁盘创建失败:" + stringR.getMsg()));
            String execked = stringR.getData();
            Assert.isTrue(execked.startsWith("Formatting"), () -> new DubboFunctionException("磁盘创建失败:" + execked));
            return R.ok("Disk Create Successfully");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentDiskClientImpl::createDisk] Create Disk Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> deleteDisk(String targetAgentIp, String absolutePath) {
        try {
            checkFileSuffix(absolutePath);
            Assert.isTrue(FileUtil.isFile(absolutePath), () -> new DubboFunctionException(absolutePath + "->" + "非文件路径!"));
            boolean del = FileUtil.del(absolutePath);
            return R.ok(String.valueOf(del));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentDiskClientImpl::deleteDisk] Delete Disk Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Integer> queryDiskSize(String targetAgentIp, String originImagePath) {
        try {
            R<String> stringR = VspRuntimeUtil.execForStr("/bin/sh " + startDependentConfig.getFilePrefix() + File.separator + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_DISK_SIZE_CALC_KEY));
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("磁盘大小查询失败:" + stringR.getMsg());
            String data = stringR.getData();
            String diskSizeStr = data.trim() + StrUtil.SPACE + originImagePath;
            Integer diskSize = Integer.parseInt(diskSizeStr);
            return R.ok(diskSize);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentDiskClientImpl::queryDiskSize] Query Disk Size Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<Void> copyDiskFile(String targetAgentIp, String originImagePath, String targetDiskPath) {
        try {
            R<String> stringR = VspRuntimeUtil.execForStr("cp " + originImagePath + " " + targetDiskPath);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("磁盘复制失败:" + stringR.getMsg()));
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentDiskClientImpl::copyDiskFile] Copy Disk File Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> attachDisk(String targetAgentIp, String vmwareUuid, String diskFilePath, String targetDev) {
        try {
            String command = StrUtil.format("virsh attach-disk --domain {} --source {} --subdriver qcow2 --targetbus virtio --persistent --target {}", vmwareUuid, diskFilePath, targetDev);
            R<String> stringR = VspRuntimeUtil.execForStr(command);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("磁盘挂载失败:" + stringR.getMsg()));
            String execked = stringR.getData();
            Assert.isTrue(execked.contains("Disk attached successfully"), () -> new DubboFunctionException("磁盘挂载失败:" + execked));
            return R.ok(execked);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentDiskClientImpl::attachDisk] Attach Disk File Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> detachDisk(String targetAgentIp, String vmwareUuid, String targetDev) {
        try {
            String command = StrUtil.format("virsh detach-disk --domain {} --target {} --persistent", vmwareUuid, targetDev);
            R<String> stringR = VspRuntimeUtil.execForStr(command);
            Assert.isTrue(stringR.checkSuccess(), () -> new DubboFunctionException("磁盘卸载失败:" + stringR.getMsg()));
            String execked = stringR.getData();
            Assert.isTrue(execked.contains("Disk detached successfully"), () -> new DubboFunctionException("磁盘卸载失败:" + execked));
            return R.ok(execked);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("[AgentDiskClientImpl::detachDisk] Detach Disk File Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    private void checkFileSuffix(String filePath) {
        String lowerCase = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        Assert.isTrue("qcow2".equals(lowerCase), () -> new BadRequestException("文件后缀校验失败, 非 'qcow2'!"));
    }
}
