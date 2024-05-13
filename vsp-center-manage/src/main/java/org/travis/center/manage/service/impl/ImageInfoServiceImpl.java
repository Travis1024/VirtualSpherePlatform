package org.travis.center.manage.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.manage.ImageInfoMapper;
import org.travis.center.common.entity.manage.ImageInfo;
import org.travis.center.manage.service.ImageInfoService;
/**
 * @ClassName ImageInfoServiceImpl
 * @Description ImageInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements ImageInfoService{

}
