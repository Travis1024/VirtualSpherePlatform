package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.enums.ImageStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.ImageInfoMapper;
import org.travis.center.common.entity.manage.ImageInfo;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.dto.ImageUploadDTO;
import org.travis.center.manage.pojo.vo.ImageUploadVO;
import org.travis.center.manage.service.ImageInfoService;
import org.travis.shared.common.constants.ImageConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.NetworkUtils;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName ImageInfoServiceImpl
 * @Description ImageInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
@Service
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements ImageInfoService{

    @Resource
    private HostInfoMapper hostInfoMapper;
    @Resource
    private AgentAssistService agentAssistService;

    @Override
    public ImageUploadVO getImageUploadInfo(ImageUploadDTO imageUploadDTO) {
        // 1.初始化镜像信息
        ImageInfo imageInfo = new ImageInfo();
        BeanUtils.copyProperties(imageUploadDTO, imageInfo);
        imageInfo.setId(SnowflakeIdUtil.nextId());
        imageInfo.setState(ImageStateEnum.UPLOADING);
        imageInfo.setStateMessage(ImageStateEnum.UPLOADING.getDisplay());
        imageInfo.setSubPath(ImageConstant.SUB_ISO_PATH_PREFIX + File.separator + imageUploadDTO.getIsoFileName());
        save(imageInfo);

        // 2. 查询在线的 Agent 服务地址
        List<String> agentIpList = agentAssistService.getHealthyHostAgentIpList();
        Assert.isFalse(agentIpList.isEmpty(), () -> new BadRequestException("未查询到在线 Host-Agent 服务!"));
        String serverAgentIp = agentIpList.get(RandomUtil.randomInt(0, agentIpList.size()));
        String serverAgentPort = SystemConstant.HOST_SERVER_PORT;
        String serverUploadUri = ImageConstant.HOST_SLICE_UPLOAD_URI;
        String serverMergeUri = ImageConstant.HOST_SLICE_MERGE_URI;

        // 3.查询 Agent-IP 对应宿主机的共享存储地址
        Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoMapper.selectOne(Wrappers.<HostInfo>lambdaQuery().eq(HostInfo::getIp, serverAgentIp)));
        Assert.isFalse(hostInfoOptional.isEmpty(), () -> new BadRequestException(String.format("未找到相关 IP: %s 匹配的宿主机信息", serverAgentIp)));
        String sharedStoragePath = hostInfoOptional.get().getSharedStoragePath();

        // 4.生成响应信息
        List<String> serverTempFilePathList = new ArrayList<>(imageUploadDTO.getSliceNumber());
        for (int i = 0; i < imageUploadDTO.getSliceNumber(); i++) {
            // /tmp/vsp/iso/1712678912389115_0
            // /tmp/vsp/iso/1712678912389115_1
            serverTempFilePathList.add(ImageConstant.TMP_ISO_PATH_PREFIX + File.separator + imageInfo.getId() + StrUtil.UNDERLINE + i);
        }
        ImageUploadVO imageUploadVO = new ImageUploadVO();
        // 1712678912389115
        imageUploadVO.setImageId(imageInfo.getId());
        // 192.168.0.202
        imageUploadVO.setServerAgentIp(serverAgentIp);
        // 22002
        imageUploadVO.setServerAgentPort(serverAgentPort);
        // /agent/file/sliceUpload
        imageUploadVO.setServerUploadUri(serverUploadUri);
        // /agent/file/sliceMerge
        imageUploadVO.setServerMergeUri(serverMergeUri);
        // /root/vsp/share/share_iso/ubuntu-22.02.iso
        imageUploadVO.setServerFilePath(sharedStoragePath + ImageConstant.SUB_ISO_PATH_PREFIX + File.separator + imageUploadDTO.getIsoFileName());
        // /tmp/vsp/iso/1712678912389115_0
        // /tmp/vsp/iso/1712678912389115_1
        imageUploadVO.setServerTempFilePathList(serverTempFilePathList);
        return imageUploadVO;
    }

    @Override
    public void changeImageInfoSuccessState(Long imageId) {
        boolean updated = update(
                Wrappers.<ImageInfo>lambdaUpdate()
                        .set(ImageInfo::getState, ImageStateEnum.READY)
                        .set(ImageInfo::getStateMessage, ImageStateEnum.READY.getDisplay())
                        .eq(ImageInfo::getId, imageId)
        );
        Assert.isTrue(updated, () -> new BadRequestException("更新失败, 未匹配到镜像文件, 请检查镜像文件信息!"));
    }

    @Override
    public void changeImageInfoErrorState(Long imageId, String errorMessage) {
        boolean updated = update(
                Wrappers.<ImageInfo>lambdaUpdate()
                        .set(ImageInfo::getState, ImageStateEnum.ERROR)
                        .set(ImageInfo::getStateMessage, ImageStateEnum.ERROR.getDisplay() + StrUtil.COLON + errorMessage)
                        .eq(ImageInfo::getId, imageId)
        );
        Assert.isTrue(updated, () -> new BadRequestException("更新失败, 未匹配到镜像文件, 请检查镜像文件信息!"));
    }

    @Override
    public List<ImageInfo> queryImageList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public ImageInfo queryOneImageById(Long imageId) {
        // TODO 检测所有的 Optional orElseThrow 使用
        Optional<ImageInfo> imageInfoOptional = Optional.ofNullable(getById(imageId));
        return imageInfoOptional.orElseThrow(() -> new BadRequestException("未找到当前镜像文件!"));
    }
}
