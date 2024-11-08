package org.travis.center.manage.pojo.pipeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.shared.common.pipeline.ProcessModel;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SnapshotInsertPipe
 * @Description SnapshotInsertPipe
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotInsertPipe implements Serializable, ProcessModel {
    private List<SnapshotInfo> latestSnapshotInfoList;
    private List<SnapshotInfo> historySnapshotInfoList;
    private String snapshotName;
    private Long vmwareId;
    private String vmwareUuid;
    private String hostIp;
    private String description;
}
