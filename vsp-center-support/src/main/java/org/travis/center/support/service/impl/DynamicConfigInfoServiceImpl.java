package org.travis.center.support.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.support.service.DynamicConfigInfoService;
/**
 * @ClassName DynamicConfigInfoServiceImpl
 * @Description DynamicConfigInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Service
public class DynamicConfigInfoServiceImpl extends ServiceImpl<DynamicConfigInfoMapper, DynamicConfigInfo> implements DynamicConfigInfoService{

}
