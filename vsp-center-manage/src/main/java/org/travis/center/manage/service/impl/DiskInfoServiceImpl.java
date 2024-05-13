package org.travis.center.manage.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.manage.DiskInfoMapper;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.manage.service.DiskInfoService;
/**
 * @ClassName DiskInfoServiceImpl
 * @Description DiskInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class DiskInfoServiceImpl extends ServiceImpl<DiskInfoMapper, DiskInfo> implements DiskInfoService{

}
