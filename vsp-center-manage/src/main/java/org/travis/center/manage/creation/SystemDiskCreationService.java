package org.travis.center.manage.creation;

import cn.hutool.core.lang.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.entity.manage.ImageInfo;
import org.travis.center.common.enums.ArchEnum;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;
import org.travis.shared.common.constants.DiskConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @ClassName SystemDiskCreationService
 * @Description SystemDiskCreationService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Service
public class SystemDiskCreationService extends AbstractCreationService{

    public SystemDiskCreationService() {
        vmwareCreateFormValue = VmwareCreateFormEnum.IMAGE.getValue();
    }

    @Transactional
    @Override
    public void createSystemDisk() {
        // 1.查询 Image 镜像信息
        Optional<ImageInfo> imageInfoOptional = Optional.ofNullable(imageInfoMapper.selectById(vmwareInsertDTO.getImageId()));
        Assert.isTrue(imageInfoOptional.isPresent(), () -> new BadRequestException("未查到镜像信息!"));
        ImageInfo imageInfo = imageInfoOptional.get();

        // 2.组装参数
        long diskId = SnowflakeIdUtil.nextId();
        // /root/vsp/share/share_image/kylin-v10.qcow2
        String originImagePath = agentAssistService.getHostSharedStoragePath() + imageInfo.getSubPath();
        // Root-Disk-89as8282nd912h.qcow2
        String diskName = DiskConstant.DISK_NAME_ROOT_PREFIX + diskId + DiskConstant.DISK_NAME_SUFFIX;
        // /share_disk/Root-Disk-89as8282nd912h.qcow2
        String subPath = DiskConstant.SUB_DISK_PATH_PREFIX + File.separator + diskName;
        // /root/vsp/share/share_disk/Root-Disk-89as8282nd912h.qcow2
        String targetDiskPath = agentAssistService.getHostSharedStoragePath() + subPath;

        // 3.Dubbo 查询 Image 镜像大小
        R<Integer> diskSizeR = agentDiskClient.queryDiskSize(hostInfo.getIp(), originImagePath);
        Assert.isTrue(diskSizeR.checkSuccess(), () -> new DubboFunctionException("Image 镜像大小查询失败!"));
        Integer diskSizeGbUnit = diskSizeR.getData();

        // 4.Dubbo-复制磁盘
        R<Void> copyDiskFileR = agentDiskClient.copyDiskFile(hostInfo.getIp(), originImagePath, targetDiskPath);
        Assert.isTrue(copyDiskFileR.checkSuccess(), () -> new DubboFunctionException("系统磁盘复制失败!"));

        // 5.组装 DiskInfo
        DiskInfo targetDiskInfo = new DiskInfo();
        targetDiskInfo.setId(diskId);
        targetDiskInfo.setName(diskName);
        targetDiskInfo.setDescription("Copy from " + imageInfo.getSubPath());
        targetDiskInfo.setSpaceSize(diskSizeGbUnit * SystemConstant.GB_UNIT);
        targetDiskInfo.setSubPath(subPath);
        targetDiskInfo.setVmwareId(vmwareInfo.getId());
        targetDiskInfo.setDiskType(DiskTypeEnum.ROOT);
        targetDiskInfo.setTargetDev("vda");
        // 设置磁盘默认为“未挂载”
        targetDiskInfo.setIsMount(DiskMountEnum.UN_MOUNTED);
        diskInfoMapper.insert(targetDiskInfo);

        diskInfo = targetDiskInfo;
    }

    @Override
    public String getXmlTemplateContent() throws IOException {
        String xmlTemplateContent = null;
        if (vmwareInsertDTO.getVmwareArch().getValue().equals(ArchEnum.X86_64.getValue())) {
            ClassPathResource resource = new ClassPathResource("template/template_kylin_amd64_image.xml");
            Path path = Paths.get(resource.getURI());
            xmlTemplateContent = new String(Files.readAllBytes(path));
        } else if (vmwareInsertDTO.getVmwareArch().getValue().equals(ArchEnum.AARCH64.getValue())) {
            ClassPathResource resource = new ClassPathResource("template/template_kylin_aarch64_image.xml");
            Path path = Paths.get(resource.getURI());
            xmlTemplateContent = new String(Files.readAllBytes(path));
        }
        return xmlTemplateContent;
    }
}
