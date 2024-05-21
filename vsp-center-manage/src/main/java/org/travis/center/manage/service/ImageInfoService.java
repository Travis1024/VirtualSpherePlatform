package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.ImageInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.ImageUploadDTO;
import org.travis.center.manage.pojo.vo.ImageUploadVO;

import java.util.List;

/**
 * @ClassName ImageInfoService
 * @Description ImageInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface ImageInfoService extends IService<ImageInfo>{
    ImageUploadVO getImageUploadInfo(ImageUploadDTO imageUploadDTO);
    void changeImageInfoSuccessState(Long imageId);
    void changeImageInfoErrorState(Long imageId, String errorMessage);
    List<ImageInfo> selectImageList();
    ImageInfo selectOneImageById(Long imageId);
}
