package org.travis.center.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.auth.AuthVmwareRelation;
import org.travis.center.common.mapper.auth.AuthVmwareRelationMapper;
import org.travis.center.auth.service.AuthVmwareRelationService;
/**
 * @ClassName AuthVmwareRelationServiceImpl
 * @Description AuthVmwareRelationServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class AuthVmwareRelationServiceImpl extends ServiceImpl<AuthVmwareRelationMapper, AuthVmwareRelation> implements AuthVmwareRelationService{

}
