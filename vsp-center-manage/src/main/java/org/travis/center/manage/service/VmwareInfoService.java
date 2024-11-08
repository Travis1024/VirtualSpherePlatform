package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.VmwareInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.pojo.dto.VmwareLoginInfoUpdateDTO;
import org.travis.center.manage.pojo.dto.VmwareMigrateDTO;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    String queryIpAddress(Long vmwareId);

    String queryIpAddressByAgent(Long vmwareId);

    Map<Long, String> batchQueryIpAddress(List<Long> vmwareIds);

    Map<Long, String> batchQueryIpAddressByAgent(List<Long> vmwareIds);

    String liveMigrate(VmwareMigrateDTO vmwareMigrateDTO);

    String offlineMigrate(VmwareMigrateDTO vmwareMigrateDTO);

    void setLoginInfo(VmwareLoginInfoUpdateDTO vmwareLoginInfoUpdateDTO);

    boolean validateVmwareSshConnect(Long vmwareId, String username, String password);
}
