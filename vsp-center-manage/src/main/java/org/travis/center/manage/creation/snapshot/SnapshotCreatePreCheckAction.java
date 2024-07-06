package org.travis.center.manage.creation.snapshot;

import org.springframework.stereotype.Service;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.manage.pojo.pipe.SnapshotInsertPipe;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

/**
 * @ClassName SnapshotPreCheckAction
 * @Description 快照创建预检查动作
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Service
public class SnapshotCreatePreCheckAction implements BusinessExecutor<SnapshotInsertPipe> {
    @Override
    public void execute(ProcessContext<SnapshotInsertPipe> context) {

    }
}
