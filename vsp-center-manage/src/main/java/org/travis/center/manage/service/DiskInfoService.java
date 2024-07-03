package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.DiskInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.DiskAttachDTO;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.util.List;

/**
 * @ClassName DiskInfoService
 * @Description DiskInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface DiskInfoService extends IService<DiskInfo>{
    DiskInfo selectOne(Long diskId);
    List<DiskInfo> selectList();
    PageResult<DiskInfo> pageSelectList(PageQuery pageQuery);
    DiskInfo createDisk(DiskInsertDTO diskInsertDTO);
    void createDiskRequest(DiskInfo diskInfo);
    void deleteDisk(Long diskId, boolean isUserDelete);
    List<DiskInfo> selectListByVmwareId(Long vmwareId);
    PageResult<DiskInfo> pageSelectListByVmwareId(PageQuery pageQuery, Long vmwareId);
    void attachDisk(DiskAttachDTO diskAttachDTO);
    void detachDisk(Long diskId);
}
