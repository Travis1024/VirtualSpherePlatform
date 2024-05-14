package org.travis.center.manage.controller;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.springframework.web.bind.annotation.*;

/**
* (VSP.VSP_VMWARE_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/VSP.VSP_VMWARE_INFO")
public class VmwareInfoController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public VmwareInfo selectOne(Integer id) {
        return null;
    }
}
