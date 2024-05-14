package org.travis.center.auth.service;

import org.travis.center.auth.pojo.dto.AuthVmwareDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthVmwareInsertDTO;
import org.travis.center.common.entity.auth.AuthVmwareRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.common.entity.manage.VmwareInfo;

import java.util.List;

/**
 * @ClassName AuthVmwareRelationService
 * @Description AuthVmwareRelationService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface AuthVmwareRelationService extends IService<AuthVmwareRelation>{
    void insertRelations(AuthVmwareInsertDTO authVmwareInsertDTO);
    void deleteRelations(AuthVmwareDeleteDTO authVmwareDeleteDTO);
    List<VmwareInfo> queryVmwareListByAuthGroup(Long authGroupId);
}
