package org.travis.center.manage.creation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;

/**
 * @ClassName IsoCreationService
 * @Description IsoCreationService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Service
public class IsoCreationService extends AbstractCreationService{

    public IsoCreationService() {
        vmwareCreateFormValue = VmwareCreateFormEnum.ISO.getValue();
    }

    @Transactional
    @Override
    public void createSystemDisk() {
        DiskInsertDTO diskInsertDTO = new DiskInsertDTO();
        diskInsertDTO.setVmwareId(vmwareInfo.getId());
        diskInsertDTO.setDiskType(DiskTypeEnum.ROOT);
        diskInsertDTO.setDescription("System disk is created automatically by the system!");
        diskInsertDTO.setSpaceSize(vmwareInsertDTO.getSystemDiskSize() * 1024L * 1024L * 1024L);
        diskInfo = diskInfoService.createDisk(diskInsertDTO, false);
    }

    @Override
    public String getXmlTemplateContent() {
        return "";
    }
}
