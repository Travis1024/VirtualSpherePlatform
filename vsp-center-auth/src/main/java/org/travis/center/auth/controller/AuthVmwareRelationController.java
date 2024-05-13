package org.travis.center.auth.controller;
import org.travis.center.common.entity.auth.AuthVmwareRelation;
import org.travis.center.auth.service.impl.AuthVmwareRelationServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* 权限组-虚拟机关联关系表(VSP.VSP_AUTH_VMWARE_RELATION)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/authVmware")
public class AuthVmwareRelationController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public AuthVmwareRelation selectOne(Integer id) {
        return null;
    }

}
