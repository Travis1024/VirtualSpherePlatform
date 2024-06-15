package org.travis.center.manage.creation;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentDiskClient;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.api.pojo.bo.HostResourceInfoBO;
import org.travis.center.common.entity.manage.*;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.*;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName AbstractCreationService
 * @Description AbstractCreationService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Slf4j
public abstract class AbstractCreationService {

    @Resource
    public HostInfoMapper hostInfoMapper;
    @Resource
    public AgentHostClient agentHostClient;
    @Resource
    public VmwareInfoMapper vmwareInfoMapper;
    @Resource
    public DiskInfoMapper diskInfoMapper;
    @Resource
    public DiskInfoService diskInfoService;
    @Resource
    public CreationHolder creationHolder;
    @Resource
    public AgentAssistService agentAssistService;
    @Resource
    public ImageInfoMapper imageInfoMapper;
    @Resource
    public AgentDiskClient agentDiskClient;
    @DubboReference
    public AgentVmwareClient agentVmwareClient;
    @Resource
    private VmwareXmlDetailsMapper vmwareXmlDetailsMapper;

    // step 0
    protected Integer vmwareCreateFormValue;
    // step 0
    protected AbstractCreationService creationService;
    // step 0
    protected VmwareInsertDTO vmwareInsertDTO;
    // step 1
    protected HostResourceInfoBO hostResourceInfoBO;
    // step 1
    protected HostInfo hostInfo;
    // step 2
    protected VmwareInfo vmwareInfo;
    // step 3
    protected DiskInfo diskInfo;


    @PostConstruct
    public void init() {
        creationHolder.addCreationService(vmwareCreateFormValue, this);
        creationService = this;
    }

    @Transactional
    public void build(VmwareInsertDTO vmwareInsertDTO) throws IOException {
        this.vmwareInsertDTO = vmwareInsertDTO;

        // 1.查询宿主机信息并验证
        creationService.stepOne();
        // 2.封装并保存虚拟机信息
        creationService.stepTwo();
        // 3.创建系统磁盘
        creationService.createSystemDisk();
        // 4.替换 xml 文件字段
        String xmlContent = creationService.stepFour();
        // 5.Dubbo-远程定义虚拟机
        R<Void> vmwareCreateR = agentVmwareClient.createVmware(hostInfo.getIp(), xmlContent, vmwareInfo.getId());
        Assert.isTrue(vmwareCreateR.checkSuccess(), () -> new DubboFunctionException("vmware create failed -> " + vmwareCreateR.getMsg()));
        // 6.xml 存储到数据库中
        creationService.stepSix(xmlContent);
        // 7.修改系统磁盘状态、修改虚拟机状态
        creationService.stepSeven();
    }

    public void stepOne() {
        Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoMapper.selectById(vmwareInsertDTO.getHostId()));
        hostInfoOptional.orElseThrow(() -> new BadRequestException("宿主机信息查询失败!"));
        HostInfo hostInfo = hostInfoOptional.get();
        this.hostInfo = hostInfo;
        this.hostResourceInfoBO = validateHostFitTheBill(hostInfo.getIp(), vmwareInsertDTO.getVcpuCurrent(), vmwareInsertDTO.getMemoryCurrent());
    }

    @Transactional
    public void stepTwo() {
        VmwareInfo vmwareInfo = new VmwareInfo();
        BeanUtils.copyProperties(vmwareInsertDTO, vmwareInfo);
        vmwareInfo.setId(SnowflakeIdUtil.nextId());
        vmwareInfo.setUuid(IdUtil.fastUUID());
        vmwareInfo.setState(VmwareStateEnum.ING_CREATE);
        vmwareInfo.setVcpuMax(hostResourceInfoBO.getVCpuAllNum());
        vmwareInfo.setMemoryMax((long) ((hostResourceInfoBO.getMemoryTotalMax() * 1.0 / SystemConstant.GB_UNIT) * 0.8) * SystemConstant.GB_UNIT);
        vmwareInfoMapper.insert(vmwareInfo);
        this.vmwareInfo = vmwareInfo;
    }

    public String stepFour() throws IOException {
        XmlParamBO xmlParamBO = new XmlParamBO();
        xmlParamBO.setName(vmwareInfo.getName());
        xmlParamBO.setUuid(vmwareInfo.getUuid());
        xmlParamBO.setMaxMemory(String.valueOf(vmwareInfo.getMemoryMax() / SystemConstant.KB_UNIT));
        xmlParamBO.setCurMemory(String.valueOf(vmwareInfo.getMemoryCurrent() / SystemConstant.KB_UNIT));
        xmlParamBO.setMaxVcpu(String.valueOf(vmwareInfo.getVcpuMax()));
        xmlParamBO.setCurVcpu(String.valueOf(vmwareInfo.getVcpuCurrent()));
        xmlParamBO.setSystemDiskPath(agentAssistService.getHostSharedStoragePath() + diskInfo.getSubPath());

        if (VmwareCreateFormEnum.ISO.getValue().equals(vmwareInfo.getCreateForm().getValue())) {
            ImageInfo imageInfo = imageInfoMapper.selectById(vmwareInsertDTO.getImageId());
            xmlParamBO.setIsoPath(agentAssistService.getHostSharedStoragePath() + imageInfo.getSubPath());
        }

        return replaceXmlParams(xmlParamBO);
    }

    @Transactional
    public void stepSix(String xmlContent) {
        VmwareXmlDetails vmwareXmlDetails = new VmwareXmlDetails();
        vmwareXmlDetails.setId(vmwareInfo.getId());
        vmwareXmlDetails.setInitXml(xmlContent);
        vmwareXmlDetailsMapper.insert(vmwareXmlDetails);
    }

    @Transactional
    public void stepSeven() {
        // 7.1.更新系统磁盘状态
        diskInfoMapper.update(Wrappers.<DiskInfo>lambdaUpdate().set(DiskInfo::getIsMount, DiskMountEnum.MOUNTED).eq(DiskInfo::getId, diskInfo.getId()));
        // 7.2.更新虚拟机状态
        vmwareInfoMapper.update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, VmwareStateEnum.SHUT_OFF).eq(VmwareInfo::getId, vmwareInfo.getId()));
    }

    private String replaceXmlParams(XmlParamBO xmlParamBO) throws IOException {
        // 1.将 XmlParamBO 转为 Map
        Map<String, Object> beanToMap = BeanUtil.beanToMap(xmlParamBO, false, true);
        // 2.获取响应的配置文件
        String xmlTemplateContent = getXmlTemplateContent();
        // 3.替换占位符
        return StrUtil.format(xmlTemplateContent, beanToMap);
    }

    private HostResourceInfoBO validateHostFitTheBill(String hostIp, Integer vcpuCurrent, Long memoryCurrent) {
        // 1.Dubbo 获取宿主机实时资源信息
        R<HostResourceInfoBO> hostResourceInfoBOR = agentHostClient.queryHostResourceInfo(hostIp);
        Assert.isTrue(hostResourceInfoBOR.checkSuccess(), () -> new BadRequestException("宿主机实时资源信息查询失败!"));

        // 2.校验宿主机是否满足虚拟机资源需求
        HostResourceInfoBO resourceInfoBO = hostResourceInfoBOR.getData();
        // 校验 CPU
        Assert.isTrue(vcpuCurrent <= resourceInfoBO.getVCpuAllNum() - resourceInfoBO.getVCpuActiveNum(), () -> new BadRequestException("虚拟机 CPU 核数已超出宿主机剩余核数, 请更换宿主机或调整虚拟机 CPU 核数! 宿主机剩余核数: " + (resourceInfoBO.getVCpuAllNum() - resourceInfoBO.getVCpuActiveNum())));
        // 校验 内存
        Assert.isTrue(memoryCurrent <= resourceInfoBO.getMemoryTotalMax() - resourceInfoBO.getMemoryTotalInUse(), () -> new BadRequestException("虚拟机内存容量已超出宿主机剩余内存, 请更换宿主机或调整虚拟机内存容量! 宿主机剩余内存: " + (resourceInfoBO.getMemoryTotalMax() - resourceInfoBO.getMemoryTotalInUse())));
        return resourceInfoBO;
    }

    public abstract void createSystemDisk();

    public abstract String getXmlTemplateContent() throws IOException;

    @Data
    static class XmlParamBO {
        private String name;
        private String uuid;
        private String maxMemory;
        private String curMemory;
        private String maxVcpu;
        private String curVcpu;
        private String systemDiskPath;
        private String isoPath;
    }
}
