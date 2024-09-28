package org.travis.center.manage.template.snapshot.build;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentSnapshotClient;
import org.travis.api.pojo.dto.SnapshotBasicInfoDTO;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.pipeline.SnapshotInsertPipe;
import org.travis.shared.common.constants.SnapshotConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    @DubboReference
    private AgentSnapshotClient agentSnapshotClient;
    @Resource
    private SnapshotInfoMapper snapshotInfoMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void execute(ProcessContext<SnapshotInsertPipe> context) {

        SnapshotInsertPipe dataModel = context.getDataModel();

        // 1.获取共享存储路径
        String sharedStoragePath = agentAssistService.getHostSharedStoragePath();

        // 2.封装虚拟机历史快照信息列表 (创建时间升序)
        List<SnapshotInfo> historySnapshotInfoList = dataModel.getHistorySnapshotInfoList();
        historySnapshotInfoList.sort((o1, o2) -> (int) (o1.getCreateTime().getTime() - o2.getCreateTime().getTime()));

        List<SnapshotBasicInfoDTO> historySnapshotBasicInfoList = new ArrayList<>();
        for (SnapshotInfo snapshotInfo : historySnapshotInfoList) {
            SnapshotBasicInfoDTO snapshotBasicInfoDTO = new SnapshotBasicInfoDTO();
            snapshotBasicInfoDTO.setTargetDev(snapshotInfo.getTargetDev());
            snapshotBasicInfoDTO.setSubPath(snapshotInfo.getSubPath());
            historySnapshotBasicInfoList.add(snapshotBasicInfoDTO);
        }
        if (historySnapshotBasicInfoList.isEmpty()) {
            return;
        }

        // 3.执行虚拟机历史版本快照合并
        R<Void> voidR = agentSnapshotClient.mergeHistorySnapshot(dataModel.getHostIp(), dataModel.getVmwareUuid(), SnapshotConstant.AUTO_SNAPSHOT_NAME, sharedStoragePath, historySnapshotBasicInfoList);
        if (voidR.checkFail()) {
            context.setNeedBreak(true);
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), voidR.getMsg()));
            return;
        }

        // 4.删除数据库历史快照数据
        snapshotInfoMapper.deleteBatchIds(historySnapshotInfoList.stream().map(SnapshotInfo::getId).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
