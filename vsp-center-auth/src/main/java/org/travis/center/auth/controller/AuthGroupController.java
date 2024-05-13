package org.travis.center.auth.controller;
import org.travis.center.common.entity.auth.AuthGroup;
import org.springframework.web.bind.annotation.*;

/**
* 权限组信息表(VSP.VSP_AUTH_GROUP)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/authGroup")
public class AuthGroupController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public AuthGroup selectOne(Integer id) {
        return null;
    }

}
