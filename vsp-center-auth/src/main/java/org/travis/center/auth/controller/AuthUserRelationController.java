package org.travis.center.auth.controller;
import org.travis.center.common.entity.auth.AuthUserRelation;
import org.travis.center.auth.service.impl.AuthUserRelationServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* 权限组-用户关联关系表(VSP.VSP_AUTH_USER_RELATION)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/authUser")
public class AuthUserRelationController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public AuthUserRelation selectOne(Integer id) {
        return null;
    }

}
