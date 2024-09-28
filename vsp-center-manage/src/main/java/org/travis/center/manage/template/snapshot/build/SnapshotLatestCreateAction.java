package org.travis.center.manage.template.snapshot.build;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentSnapshotClient;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.manage.pojo.pipeline.SnapshotInsertPipe;
import org.travis.shared.common.constants.SnapshotConstant;
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
        R<Void> createSnapshotR = agentSnapshotClient.createSnapshot(dataModel.getHostIp(), dataModel.getVmwareUuid(), SnapshotConstant.AUTO_SNAPSHOT_NAME);
        if (createSnapshotR.checkFail()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), createSnapshotR.getMsg()));
            context.setNeedBreak(true);
        }
    }
}
