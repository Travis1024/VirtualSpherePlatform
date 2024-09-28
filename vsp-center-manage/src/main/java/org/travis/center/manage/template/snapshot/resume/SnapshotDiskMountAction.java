package org.travis.center.manage.template.snapshot.resume;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.pipeline.SnapshotResumePipe;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName SnapshotDiskMountAction
 * @Description 挂载原有虚拟机磁盘 + 重启虚拟机
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class SnapshotDiskMountAction implements BusinessExecutor<SnapshotResumePipe> {
    @DubboReference
    private AgentVmwareClient agentVmwareClient;
    @Resource
    private AgentAssistService agentAssistService;

    @Override
    public void execute(ProcessContext<SnapshotResumePipe> context) {
        SnapshotResumePipe dataModel = context.getDataModel();

        // 1.将原有磁盘挂载到虚拟机
        Set<String> diskPathSet = dataModel.getDiskInfoMap().values().stream().map(DiskInfo::getSubPath).collect(Collectors.toSet());
        R<Void> mountR = agentVmwareClient.diskMount(dataModel.getHostIp(), dataModel.getVmwareInfo().getUuid(), agentAssistService.getHostSharedStoragePath(), diskPathSet);
        log.info("虚拟机磁盘挂载结果：{}", JSONUtil.toJsonStr(mountR));
        if (mountR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机磁盘挂载失败！"));
            context.setNeedBreak(true);
            return;
        }

        // 2.启动虚拟机
        R<String> startR = agentVmwareClient.startVmware(dataModel.getHostIp(), dataModel.getVmwareInfo().getUuid());
        log.info("虚拟机启动结果：{}", JSONUtil.toJsonStr(startR));
        if (startR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机启动失败！"));
            context.setNeedBreak(true);
        }
    }
}
