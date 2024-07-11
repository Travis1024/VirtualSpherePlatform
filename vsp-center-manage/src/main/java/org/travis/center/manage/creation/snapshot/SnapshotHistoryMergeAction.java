package org.travis.center.manage.creation.snapshot;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentSnapshotClient;
import org.travis.api.pojo.dto.SnapshotBasicInfoDTO;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.pipe.SnapshotInsertPipe;
import org.travis.center.manage.service.SnapshotInfoService;
import org.travis.shared.common.constants.SnapshotConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SnapshotHistoryMergeAction
 * @Description 快照历史版本合并
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Service
public class SnapshotHistoryMergeAction implements BusinessExecutor<SnapshotInsertPipe> {
    @Resource
    private AgentAssistService agentAssistService;
    @Resource
    private AgentSnapshotClient agentSnapshotClient;
    @Resource
    private SnapshotInfoService snapshotInfoService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void execute(ProcessContext<SnapshotInsertPipe> context) {

        SnapshotInsertPipe dataModel = context.getDataModel();

        // 1.获取共享存储路径
        String sharedStoragePath = agentAssistService.getHostSharedStoragePath();

        // 2.封装虚拟机历史快照信息列表
        List<SnapshotInfo> historySnapshotInfoList = dataModel.getHistorySnapshotInfoList();
        List<SnapshotBasicInfoDTO> historySnapshotBasicInfoList = new ArrayList<>();
        for (SnapshotInfo snapshotInfo : historySnapshotInfoList) {
            SnapshotBasicInfoDTO snapshotBasicInfoDTO = new SnapshotBasicInfoDTO();
            snapshotBasicInfoDTO.setTargetDev(snapshotInfo.getTargetDev());
            snapshotBasicInfoDTO.setSubPath(snapshotInfo.getSubPath());
            historySnapshotBasicInfoList.add(snapshotBasicInfoDTO);
        }

        // 3.执行虚拟机历史版本快照合并
        R<Void> voidR = agentSnapshotClient.mergeHistorySnapshot(dataModel.getHostIp(), dataModel.getVmwareUuid(), SnapshotConstant.AUTO_SNAPSHOT_NAME, sharedStoragePath, historySnapshotBasicInfoList);
        if (voidR.checkFail()) {
            context.setNeedBreak(true);
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), voidR.getMsg()));
            return;
        }

        // 4.删除数据库历史快照数据
        snapshotInfoService.removeBatchByIds(historySnapshotInfoList.stream().map(SnapshotInfo::getId).collect(Collectors.toList()));
    }
}