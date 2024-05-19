package org.travis.center.manage.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.manage.service.VmwareInfoService;
/**
 * @ClassName VmwareInfoServiceImpl
 * @Description VmwareInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Service
public class VmwareInfoServiceImpl extends ServiceImpl<VmwareInfoMapper, VmwareInfo> implements VmwareInfoService{

}
