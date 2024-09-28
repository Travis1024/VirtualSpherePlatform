package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.SnapshotInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.SnapshotInsertDTO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.util.List;

/**
 * @ClassName SnapshotInfoService
 * @Description SnapshotInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/5
 */
public interface SnapshotInfoService extends IService<SnapshotInfo>{
    List<SnapshotInfo> selectSnapshotList();

    PageResult<SnapshotInfo> pageSelectSnapshotList(PageQuery pageQuery);

    void createSnapshotInfo(SnapshotInsertDTO snapshotInsertDTO);

    void resumeSnapshot(Long vmwareId);
}
