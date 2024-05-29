package org.travis.center.manage.creation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.enums.ArchEnum;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public String getXmlTemplateContent() throws IOException {
        String xmlTemplateContent = null;
        if (vmwareInsertDTO.getVmwareArch().getValue().equals(ArchEnum.X86_64.getValue())) {
            ClassPathResource resource = new ClassPathResource("template/template_kylin_amd64_iso.xml");
            Path path = Paths.get(resource.getURI());
            xmlTemplateContent = new String(Files.readAllBytes(path));
        } else if (vmwareInsertDTO.getVmwareArch().getValue().equals(ArchEnum.AARCH64.getValue())) {
            ClassPathResource resource = new ClassPathResource("template/template_kylin_aarch64_iso.xml");
            Path path = Paths.get(resource.getURI());
            xmlTemplateContent = new String(Files.readAllBytes(path));
        }
        return xmlTemplateContent;
    }
}
