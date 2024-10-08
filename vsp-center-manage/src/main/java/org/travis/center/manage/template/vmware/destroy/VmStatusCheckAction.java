package org.travis.center.manage.template.vmware.destroy;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.manage.pojo.pipeline.VmwareDestroyPipe;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.PipelineProcessException;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

/**
 * @ClassName VmStatusCheckAction
 * @Description 判断虚拟机状态（强制关闭）
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class VmStatusCheckAction implements BusinessExecutor<VmwareDestroyPipe> {

    @DubboReference
    private AgentVmwareClient agentVmwareClient;

    @Override
    public void execute(ProcessContext<VmwareDestroyPipe> context) {
        VmwareDestroyPipe dataModel = context.getDataModel();
        VmwareInfo vmwareInfo = dataModel.getVmwareInfo();
        HostInfo hostInfo = dataModel.getHostInfo();

        try {
            log.info("虚拟机状态检查：{}", JSONUtil.toJsonStr(vmwareInfo));
            if (!VmwareStateEnum.SHUT_OFF.equals(vmwareInfo.getState())) {
                R<String> destroyed = agentVmwareClient.destroyVmware(hostInfo.getIp(), vmwareInfo.getUuid());
                log.info("虚拟机关闭结果：{}", JSONUtil.toJsonStr(destroyed));
                if (destroyed.checkFail()) {
                    throw new PipelineProcessException(destroyed.getMsg());
                }
            }
        } catch (PipelineProcessException pipelineProcessException) {
            log.error("虚拟机状态处理失败：{}", pipelineProcessException.getMessage());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机状态处理失败：" + pipelineProcessException.getMessage()));
            context.setNeedBreak(true);
        } catch (Exception e) {
            log.error("「未知异常」虚拟机状态处理失败：{}", e.toString());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "「未知异常」虚拟机状态处理失败：" + e.getMessage()));
            context.setNeedBreak(true);
        }
    }
}
