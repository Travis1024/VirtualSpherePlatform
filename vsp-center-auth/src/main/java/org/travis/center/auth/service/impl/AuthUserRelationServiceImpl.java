package org.travis.center.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.auth.AuthUserRelation;
import org.travis.center.common.mapper.auth.AuthUserRelationMapper;
import org.travis.center.auth.service.AuthUserRelationService;
/**
 * @ClassName AuthUserRelationServiceImpl
 * @Description AuthUserRelationServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class AuthUserRelationServiceImpl extends ServiceImpl<AuthUserRelationMapper, AuthUserRelation> implements AuthUserRelationService{

}
