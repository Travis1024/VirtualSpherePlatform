package org.travis.center.manage.service;

import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.manage.VmwareInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName VmwareInfoService
 * @Description VmwareInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
public interface VmwareInfoService extends IService<VmwareInfo>{
    VmwareInfo selectOne(Long id);

    List<VmwareInfo> selectAll();

    PageResult<VmwareInfo> pageSelectList(PageQuery pageQuery);

    void createVmwareInfo(VmwareInsertDTO vmwareInsertDTO) throws IOException;

    List<VmwareErrorVO> startVmware(List<Long> vmwareIds);

    List<VmwareErrorVO> suspendVmware(List<Long> vmwareIds);

    List<VmwareErrorVO> resumeVmware(List<Long> vmwareIds);

    List<VmwareErrorVO> shutdownVmware(List<Long> vmwareIds);

    List<VmwareErrorVO> destroyVmware(List<Long> vmwareIds);

    List<VmwareErrorVO> deleteVmware(List<Long> vmwareIds);

    VmwareErrorVO deleteOneById(Long vmwareId);

    void modifyVmwareMemory(Long vmwareId, Long memory);

    void modifyVmwareVcpuNumber(Long vmwareId, Integer vcpuNumber);

    String queryVncAddress(Long vmwareId);

    void correctVmwareState();
}
