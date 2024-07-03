package org.travis.center.manage.creation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.enums.ArchEnum;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;
import org.travis.shared.common.constants.DiskConstant;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import java.io.File;
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
public class IsoCreationService extends AbstractCreationService {

    public IsoCreationService() {
        vmwareCreateFormEnum = VmwareCreateFormEnum.ISO;
    }

    @Transactional
    @Override
    public void createSystemDisk() {
        // 1.拼接参数
        long diskId = SnowflakeIdUtil.nextId();
        // Root-Disk-89as8282nd912h
        String diskName = DiskConstant.DISK_NAME_ROOT_PREFIX + diskId;
        // /share_disk/Root-Disk-89as8282nd912h.qcow2
        String subPath = DiskConstant.SUB_DISK_PATH_PREFIX + File.separator + diskName + DiskConstant.DISK_NAME_SUFFIX;

        // 2.组装 DiskInfo
        DiskInfo targetDiskInfo = new DiskInfo();
        targetDiskInfo.setId(diskId);
        targetDiskInfo.setName(diskName);
        targetDiskInfo.setDescription("System disk is created automatically by the system!");
        targetDiskInfo.setSpaceSize(vmwareInsertDTO.getSystemDiskSize() * 1024L * 1024L * 1024L);
        targetDiskInfo.setSubPath(subPath);
        targetDiskInfo.setVmwareId(vmwareInfo.getId());
        targetDiskInfo.setDiskType(DiskTypeEnum.ROOT);
        targetDiskInfo.setTargetDev("vda");
        targetDiskInfo.setIsMount(DiskMountEnum.UN_MOUNTED);

        // 3.创建磁盘
        diskInfoService.createDiskRequest(targetDiskInfo);

        this.diskInfo = targetDiskInfo;
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
