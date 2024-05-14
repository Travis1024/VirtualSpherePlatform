package org.travis.center.manage.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.manage.VmwareXmlDetailsMapper;
import org.travis.center.common.entity.manage.VmwareXmlDetails;
import org.travis.center.manage.service.VmwareXmlDetailsService;
/**
 * @ClassName VmwareXmlDetailsServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Service
public class VmwareXmlDetailsServiceImpl extends ServiceImpl<VmwareXmlDetailsMapper, VmwareXmlDetails> implements VmwareXmlDetailsService{

}
