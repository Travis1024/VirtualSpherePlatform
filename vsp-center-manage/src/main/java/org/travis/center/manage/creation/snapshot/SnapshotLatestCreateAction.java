package org.travis.center.manage.creation.snapshot;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentSnapshotClient;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.manage.pojo.pipe.SnapshotInsertPipe;
import org.travis.center.manage.service.SnapshotInfoService;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;

/**
 * @ClassName SnapshotLatestCreateAction
 * @Description 快照最新版本创建
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Service
public class SnapshotLatestCreateAction implements BusinessExecutor<SnapshotInsertPipe> {
    @Resource
    private AgentSnapshotClient agentSnapshotClient;
    @Resource
    private SnapshotInfoMapper snapshotInfoMapper;

    @Transactional
    @Override
    public void execute(ProcessContext<SnapshotInsertPipe> context) {
        SnapshotInsertPipe dataModel = context.getDataModel();

        // 持久化新快照列表到数据库
        dataModel.getLatestSnapshotInfoList().forEach(snapshotInfo -> snapshotInfoMapper.insert(snapshotInfo));

        // 创建最新快照
        R<Void> createSnapshotR = agentSnapshotClient.createSnapshot(dataModel.getHostIp(), dataModel.getVmwareUuid(), dataModel.getSnapshotName());
        if (createSnapshotR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), createSnapshotR.getMsg()));
            context.setNeedBreak(true);
        }
    }
}
