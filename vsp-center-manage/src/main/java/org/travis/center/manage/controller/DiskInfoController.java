package org.travis.center.manage.controller;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.manage.service.DiskInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* (VSP.VSP_DISK_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/disk")
public class DiskInfoController {
/**
* 服务对象
*/
    @Resource
    private DiskInfoService diskInfoService;

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public DiskInfo selectOne(Integer id) {
        return null;
    }

}
