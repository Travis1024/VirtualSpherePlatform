package org.travis.agent.web.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.travis.agent.web.config.StartDependentConfig;
import org.travis.shared.common.constants.AgentDependentConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.utils.VspRuntimeUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VmwareStateAggregateHandler
 * @Description VmwareStateAggregateHandler
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Slf4j
@Component
public class VmwareStateAggregateHandler {

    @Resource
    public StartDependentConfig startDependentConfig;

    public Map<String, String> queryVmwareUuidStatesMap() {
        R<List<String>> listR = VspRuntimeUtil.execForLines("/bin/sh " + startDependentConfig.getFilePrefix() + File.separator + startDependentConfig.getFiles().get(AgentDependentConstant.INIT_QUERY_VM_STATES_KEY));
        if (listR.checkFail()) {
            log.error("[VmwareStateAggregateHandler::queryVmwareUuidStatesMap] execForLines error: {}", listR.getMsg());
            return Collections.emptyMap();
        }
        List<String> execkedForLineList = listR.getData();

        Map<String, String> vmwareUuidStatesMap = new HashMap<>();
        for (String line : execkedForLineList) {
            String[] lineSplit = line.split(StrUtil.COLON);
            // 如果 ”uuid:state“ 格式分割失败，则打印错误日志
            if (lineSplit.length != 2) {
                log.error("[VmwareStateAggregateHandler::queryVmwareUuidStatesMap] line is invalid: {}]", line);
                continue;
            }
            vmwareUuidStatesMap.put(lineSplit[0], lineSplit[1]);
        }
        return vmwareUuidStatesMap;
    }
}
