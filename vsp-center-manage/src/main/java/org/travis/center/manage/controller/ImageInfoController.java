package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.entity.manage.ImageInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.ImageUploadDTO;
import org.travis.center.manage.pojo.vo.ImageUploadVO;
import org.travis.center.manage.service.ImageInfoService;
import org.travis.center.support.aspect.Log;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.utils.VspStrUtil;

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

    @Log(title = "上传新增镜像信息 & 获取镜像切片文件上传地址")
    @Operation(summary = "上传新增镜像信息 & 获取镜像切片文件上传地址")
    @PostMapping("/preUpload")
    public ImageUploadVO getImageUploadInfo(@Validated @RequestBody ImageUploadDTO imageUploadDTO) {
        VspStrUtil.trimStr(imageUploadDTO);
        return imageInfoService.getImageUploadInfo(imageUploadDTO);
    }

    @Log(title = "删除镜像信息及文件", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除镜像信息及文件")
    @DeleteMapping("/delete")
    public void deleteImageInfo(@RequestParam("imageId") Long imageId) {
        imageInfoService.deleteImageInfo(imageId);
    }

    @Log(title = "修改镜像文件上传状态为[就绪]", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "修改镜像文件上传状态为[就绪]")
    @PutMapping("/success")
    public void changeImageInfoSuccessState(@RequestParam("imageId") Long imageId) {
        imageInfoService.changeImageInfoSuccessState(imageId);
    }

    @Log(title = "修改镜像文件上传状态为[异常]", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "修改镜像文件上传状态为[异常]")
    @PutMapping("/error")
    public void changeImageInfoErrorState(@RequestParam("imageId") Long imageId, @RequestParam("message") String errorMessage) {
        imageInfoService.changeImageInfoErrorState(imageId, errorMessage);
    }

    @Log(title = "查询镜像文件列表信息", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询镜像文件列表信息")
    @GetMapping("/select")
    public List<ImageInfo> selectImageList() {
        return imageInfoService.selectImageList();
    }

    @Log(title = "分页查询镜像文件列表信息", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询镜像文件列表信息")
    @PostMapping("/pageSelect")
    public PageResult<ImageInfo> pageSelectImageList(@Validated @RequestBody PageQuery pageQuery) {
        return imageInfoService.pageSelectImageList(pageQuery);
    }

    @Log(title = "根据镜像ID查询镜像信息", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "根据镜像ID查询镜像信息")
    @GetMapping("/selectOne")
    public ImageInfo selectOneImageById(@RequestParam("imageId") Long imageId) {
        return imageInfoService.selectOneImageById(imageId);
    }
}
