package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
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

    @Resource
    private DiskInfoService diskInfoService;

    @Operation(summary = "根据磁盘ID查询磁盘信息")
    @GetMapping("/selectOne")
    public DiskInfo selectOne(@RequestParam("diskId") Long diskId) {
        return diskInfoService.selectOne(diskId);
    }
}
