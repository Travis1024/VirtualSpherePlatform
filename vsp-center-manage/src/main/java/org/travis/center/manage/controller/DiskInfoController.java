package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.DiskAttachDTO;
import org.travis.center.manage.pojo.dto.DiskInsertDTO;
import org.travis.center.manage.pojo.dto.DiskPageSelectByVmwareDTO;
import org.travis.center.manage.service.DiskInfoService;
import org.springframework.web.bind.annotation.*;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.aspect.RequestLock;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
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

    @Log(title = "根据磁盘ID查询磁盘信息", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "根据磁盘ID查询磁盘信息")
    @GetMapping("/selectOne")
    public DiskInfo selectOne(@RequestParam("diskId") Long diskId) {
        return diskInfoService.selectOne(diskId);
    }

    @Log(title = "查询磁盘信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询磁盘信息列表")
    @GetMapping("/select")
    public List<DiskInfo> selectList() {
        return diskInfoService.selectList();
    }

    @Log(title = "分页查询磁盘信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询磁盘信息列表")
    @PostMapping("/pageSelect")
    public PageResult<DiskInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return diskInfoService.pageSelectList(pageQuery);
    }

    @Log(title = "根据虚拟机ID查询所属磁盘信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "根据虚拟机ID查询所属磁盘信息列表")
    @GetMapping("/selectByVmwareId")
    public List<DiskInfo> selectListByVmwareId(@RequestParam("vmwareId") Long vmwareId) {
        return diskInfoService.selectListByVmwareId(vmwareId);
    }

    @Log(title = "根据虚拟机ID分页查询所属磁盘信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "根据虚拟机ID分页查询所属磁盘信息列表")
    @PostMapping("/pageSelectByVmwareId")
    public PageResult<DiskInfo> pageSelectListByVmwareId(@Validated @RequestBody DiskPageSelectByVmwareDTO diskPageSelectByVmwareDTO) {
        return diskInfoService.pageSelectListByVmwareId(diskPageSelectByVmwareDTO.getPageQuery(), diskPageSelectByVmwareDTO.getVmwareId());
    }

    @RequestLock
    @Log(title = "创建新数据磁盘信息", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "创建新数据磁盘信息")
    @PostMapping("/create")
    public DiskInfo createDisk(@Validated @RequestBody DiskInsertDTO diskInsertDTO) {
        return diskInfoService.createDisk(diskInsertDTO, true);
    }

    @RequestLock
    @Log(title = "数据磁盘挂载", businessType = BusinessTypeEnum.OTHER)
    @Operation(summary = "数据磁盘挂载")
    @PostMapping("/attach")
    public void attachDisk(@Validated @RequestBody DiskAttachDTO diskAttachDTO) {
        diskInfoService.attachDisk(diskAttachDTO);
    }

    @RequestLock
    @Log(title = "数据磁盘卸载", businessType = BusinessTypeEnum.OTHER)
    @Operation(summary = "数据磁盘卸载")
    @GetMapping("/detach")
    public void detachDisk(@RequestLockKey @NotNull(message = "磁盘ID不能为空!") @RequestParam("diskId") Long diskId) {
        diskInfoService.detachDisk(diskId);
    }

    @RequestLock
    @Log(title = "删除磁盘信息", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除磁盘信息")
    @DeleteMapping("/delete")
    public void deleteDisk(@RequestLockKey @RequestParam("diskId") Long diskId) {
        diskInfoService.deleteDisk(diskId, true);
    }
}
