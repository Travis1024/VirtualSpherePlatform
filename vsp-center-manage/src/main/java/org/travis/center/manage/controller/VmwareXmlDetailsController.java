package org.travis.center.manage.controller;
import org.travis.center.common.entity.manage.VmwareXmlDetails;
import org.travis.center.manage.service.impl.VmwareXmlDetailsServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* (VSP.VSP_VMWARE_XML_DETAILS)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/vmwareXml")
public class VmwareXmlDetailsController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public VmwareXmlDetails selectOne(Integer id) {
        return null;
    }
}
