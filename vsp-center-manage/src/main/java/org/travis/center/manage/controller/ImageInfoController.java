package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.ImageInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.manage.pojo.dto.ImageUploadDTO;
import org.travis.center.manage.pojo.vo.ImageUploadVO;
import org.travis.center.manage.service.ImageInfoService;

import javax.annotation.Resource;
import java.util.List;

/**
* (VSP.VSP_IMAGE_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/image")
public class ImageInfoController {

    @Resource
    private ImageInfoService imageInfoService;

    @Operation(summary = "获取镜像文件上传地址及信息")
    @PostMapping("/preUpload")
    private ImageUploadVO getImageUploadInfo(@Validated @RequestBody ImageUploadDTO imageUploadDTO) {
        return imageInfoService.getImageUploadInfo(imageUploadDTO);
    }

    @Operation(summary = "修改镜像文件上传状态为[就绪]")
    @GetMapping("/success")
    private void changeImageInfoSuccessState(@RequestParam("imageId") Long imageId) {
        imageInfoService.changeImageInfoSuccessState(imageId);
    }
}
