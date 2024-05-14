package org.travis.center.auth.service;

import org.travis.center.auth.pojo.dto.AuthGroupInsertDTO;
import org.travis.center.auth.pojo.dto.AuthGroupUpdateDTO;
import org.travis.center.common.entity.auth.AuthGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @ClassName AuthGroupService
 * @Description AuthGroupService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface AuthGroupService extends IService<AuthGroup>{
    List<AuthGroup> queryCurrentUserAuthGroup();
    AuthGroup insertOneAuthGroup(AuthGroupInsertDTO authGroupInsertDTO);
    void updateOneAuthGroup(AuthGroupUpdateDTO authGroupUpdateDTO);
}
