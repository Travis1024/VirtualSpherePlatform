package org.travis.agent.web.service.dubbo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.agent.AgentImageClient;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.DubboFunctionException;

/**
 * @ClassName AgentImageClientImpl
 * @Description AgentImageClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/23
 */
@Slf4j
@DubboService
public class AgentImageClientImpl implements AgentImageClient {
    @Override
    public R<String> deleteImage(String targetAgentIp, String path) {
        try {
            Assert.isTrue(FileUtil.isFile(path), () -> new DubboFunctionException(path + " -> " + "非文件路径!"));
            boolean del = FileUtil.del(path);
            return R.ok(String.valueOf(del));
        } catch (Exception e) {
            log.error("[AgentImageClientImpl::deleteImage] Delete Image Error! -> {}", e.getMessage());
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
