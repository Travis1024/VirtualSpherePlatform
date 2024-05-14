package org.travis.center.auth.service;

import org.travis.center.auth.pojo.dto.AuthUserDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthUserInsertDTO;
import org.travis.center.common.entity.auth.AuthUserRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.common.entity.auth.User;

import java.util.List;

/**
 * @ClassName AuthUserRelationService
 * @Description AuthUserRelationService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface AuthUserRelationService extends IService<AuthUserRelation>{
    void insertRelations(AuthUserInsertDTO authUserInsertDTO);
    void deleteRelations(AuthUserDeleteDTO authUserDeleteDTO);
    List<User> queryUserListByAuthGroup(Long authGroupId);
}
