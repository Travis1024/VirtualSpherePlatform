package org.travis.host.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentDiskClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;

/**
 * @ClassName AgentDiskClientImpl
 * @Description AgentDiskClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/23
 */
@Slf4j
@DubboService
public class AgentDiskClientImpl implements AgentDiskClient {
    @Override
    public R<String> createDisk(String targetAgentIp, String path, Long unitGbSize) {
        try {
            // TODO 测试命令执行
            RuntimeUtil.execForStr("qemu-img create -f qcow2 -o preallocation=off " + path + " " + unitGbSize.toString() + "G");
            return R.ok("Disk Create Successfully");
        } catch (Exception e) {
            log.error("[AgentDiskClientImpl::createDisk] Create Disk Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public R<String> deleteDisk(String targetAgentIp, String absolutePath) {
        try {
            // TODO 测试命令执行
            Assert.isTrue(FileUtil.isFile(absolutePath), () -> new DubboFunctionException(absolutePath + "->" + "非文件路径!"));
            boolean del = FileUtil.del(absolutePath);
            return R.ok(String.valueOf(del));
        } catch (Exception e) {
            log.error("[AgentDiskClientImpl::deleteDisk] Delete Disk Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
