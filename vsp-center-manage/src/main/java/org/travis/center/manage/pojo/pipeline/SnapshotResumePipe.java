package org.travis.center.manage.pojo.pipeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.shared.common.pipeline.ProcessModel;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName SnapshotResumePipe
 * @Description SnapshotResumePipe
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotResumePipe implements Serializable, ProcessModel {
    private Map<String, DiskInfo> diskInfoMap;
    private Map<String, SnapshotInfo> snapshotInfoMap;
    private VmwareInfo vmwareInfo;
    private String hostIp;
}
