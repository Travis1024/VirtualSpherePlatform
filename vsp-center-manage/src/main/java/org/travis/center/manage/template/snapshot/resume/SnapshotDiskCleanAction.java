package org.travis.center.manage.template.snapshot.resume;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentSnapshotClient;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.pipeline.SnapshotResumePipe;
import org.travis.shared.common.constants.SnapshotConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName SnapshotDiskCleanAction
 * @Description 删除原有快照信息 + 删除快照文件 + 删除数据库快照记录
 * @Author Travis
 * @Data 2024/09
 */
@Service
public class SnapshotDiskCleanAction implements BusinessExecutor<SnapshotResumePipe> {

    @DubboReference
    private AgentVmwareClient agentVmwareClient;
    @DubboReference
    private AgentSnapshotClient agentSnapshotClient;
    @Resource
    private AgentAssistService agentAssistService;
    @Resource
    private SnapshotInfoMapper snapshotInfoMapper;

    @Transactional
    @Override
    public void execute(ProcessContext<SnapshotResumePipe> context) {
        SnapshotResumePipe dataModel = context.getDataModel();

        // 1.删除恢复前的快照信息
        R<Void> deleteSnapshotR = agentSnapshotClient.deleteSnapshot(dataModel.getHostIp(), dataModel.getVmwareInfo().getUuid(), SnapshotConstant.AUTO_SNAPSHOT_NAME);
        if (deleteSnapshotR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "删除恢复前的快照信息失败！"));
            context.setNeedBreak(true);
            return;
        }

        // 2.删除快照文件
        String hostSharedStoragePath = agentAssistService.getHostSharedStoragePath();
        Set<String> absolutePathSet = dataModel.getSnapshotInfoMap().values().stream().map(tmp -> hostSharedStoragePath + tmp.getSubPath()).collect(Collectors.toSet());
        R<Void> deleteSnapshotFileR = agentSnapshotClient.deleteSnapshotFile(dataModel.getHostIp(), absolutePathSet);
        if (deleteSnapshotFileR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "删除快照文件失败！"));
            context.setNeedBreak(true);
            return;
        }

        // 3.删除数据库快照记录
        snapshotInfoMapper.deleteBatchIds(dataModel.getSnapshotInfoMap().values().stream().map(SnapshotInfo::getId).collect(Collectors.toSet()));
    }
}
