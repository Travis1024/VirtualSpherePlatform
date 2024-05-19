package org.travis.center.manage.controller;
import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.entity.manage.ImageInfo;
import org.travis.center.manage.pojo.dto.ImageUploadDTO;
import org.travis.center.manage.pojo.vo.ImageUploadVO;
import org.travis.center.manage.service.ImageInfoService;
import org.travis.shared.common.exceptions.BadRequestException;

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
    public ImageUploadVO getImageUploadInfo(@Validated @RequestBody ImageUploadDTO imageUploadDTO) {
        return imageInfoService.getImageUploadInfo(imageUploadDTO);
    }

    @Operation(summary = "修改镜像文件上传状态为[就绪]")
    @PutMapping("/success")
    public void changeImageInfoSuccessState(@RequestParam("imageId") Long imageId) {
        imageInfoService.changeImageInfoSuccessState(imageId);
    }

    @Operation(summary = "修改镜像文件上传状态为[异常]")
    @PutMapping("/error")
    public void changeImageInfoErrorState(@RequestParam("imageId") Long imageId, @RequestParam("message") String errorMessage) {
        imageInfoService.changeImageInfoErrorState(imageId, errorMessage);
    }

    @Operation(summary = "查询镜像文件列表信息")
    @GetMapping("/query")
    public List<ImageInfo> queryImageList() {
        return imageInfoService.queryImageList();
    }

    @Operation(summary = "根据镜像ID查询镜像信息")
    @GetMapping("/queryOne")
    public ImageInfo queryOneImageById(@RequestParam("imageId") Long imageId) {
        return imageInfoService.queryOneImageById(imageId);
    }
}
