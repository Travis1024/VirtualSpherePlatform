package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.springframework.web.bind.annotation.*;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.annotation.Resource;
import java.util.List;

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

    @Operation(summary = "查询磁盘信息列表")
    @GetMapping("/select")
    public List<DiskInfo> selectList() {
        return diskInfoService.selectList();
    }

    @Operation(summary = "分页查询磁盘信息列表")
    @PostMapping("/pageSelect")
    public PageResult<DiskInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return diskInfoService.pageSelectList(pageQuery);
    }

    @Operation(summary = "创建新磁盘信息")
    @PostMapping("/create")
    public DiskInfo createDisk(@Validated @RequestBody DiskInsertDTO diskInsertDTO) {
        return diskInfoService.createDisk(diskInsertDTO);
    }

}
