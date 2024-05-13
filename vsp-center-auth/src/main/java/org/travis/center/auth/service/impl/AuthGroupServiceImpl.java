package org.travis.center.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.auth.AuthGroup;
import org.travis.center.common.mapper.auth.AuthGroupMapper;
import org.travis.center.auth.service.AuthGroupService;
/**
 * @ClassName AuthGroupServiceImpl
 * @Description AuthGroupServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class AuthGroupServiceImpl extends ServiceImpl<AuthGroupMapper, AuthGroup> implements AuthGroupService{

}
