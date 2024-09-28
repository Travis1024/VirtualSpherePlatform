package org.travis.center.manage.template.snapshot.resume;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.manage.pojo.pipeline.SnapshotResumePipe;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

/**
 * @ClassName SnapshotDiskUnmountAction
 * @Description 虚拟机关闭 + 移除虚拟机硬盘
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class SnapshotDiskUnmountAction implements BusinessExecutor<SnapshotResumePipe> {

    @DubboReference
    private AgentVmwareClient agentVmwareClient;

    @Override
    public void execute(ProcessContext<SnapshotResumePipe> context) {
        SnapshotResumePipe dataModel = context.getDataModel();

        // 1.判断虚拟机当前状态, 关闭虚拟机
        VmwareInfo vmwareInfo = dataModel.getVmwareInfo();
        if (!VmwareStateEnum.SHUT_OFF.equals(vmwareInfo.getState())) {
            R<String> shutdownR = agentVmwareClient.shutdownVmware(dataModel.getHostIp(), vmwareInfo.getUuid());
            log.info("虚拟机关闭结果：{}", JSONUtil.toJsonStr(shutdownR));
            if (shutdownR.checkFail()) {
                context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机关闭失败，快照恢复失败！"));
                context.setNeedBreak(true);
                return;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机关闭后休眠失败！"));
                context.setNeedBreak(true);
                return;
            }
        }

        // 2.移除虚拟机所有挂载的硬盘
        R<Void> unmountR = agentVmwareClient.diskUnmount(dataModel.getHostIp(), vmwareInfo.getUuid(), dataModel.getSnapshotInfoMap().keySet());
        log.info("虚拟机移除硬盘结果：{}", JSONUtil.toJsonStr(unmountR));
        if (unmountR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机移除硬盘失败，快照恢复失败！"));
            context.setNeedBreak(true);
        }
    }
}
