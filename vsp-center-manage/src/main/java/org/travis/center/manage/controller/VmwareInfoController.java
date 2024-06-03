package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.center.support.aspect.Log;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
* (VSP.VSP_VMWARE_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/vmware")
public class VmwareInfoController {

    @Resource
    private VmwareInfoService vmwareInfoService;

    @Log(title = "通过主键查询单条虚拟机数据", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "通过主键查询单条虚拟机数据")
    @GetMapping("/selectOne")
    public VmwareInfo selectOne(@RequestParam("id") Long id) {
        return vmwareInfoService.selectOne(id);
    }

    @Log(title = "查询虚拟机信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询虚拟机信息列表")
    @GetMapping("/select")
    public List<VmwareInfo> selectAll() {
        return vmwareInfoService.selectAll();
    }

    @Log(title = "分页查询虚拟机信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询虚拟机信息列表")
    @GetMapping("/pageSelect")
    public PageResult<VmwareInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return vmwareInfoService.pageSelectList(pageQuery);
    }

    @Log(title = "创建虚拟机", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "创建虚拟机")
    @PostMapping("/create")
    public void createVmwareInfo(@Validated @RequestBody VmwareInsertDTO vmwareInsertDTO) throws IOException {
        vmwareInfoService.createVmwareInfo(vmwareInsertDTO);
    }

    @Log(title = "删除虚拟机", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除虚拟机")
    @DeleteMapping("/delete")
    public List<VmwareErrorVO> deleteVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.deleteVmware(vmwareIds);
    }

    @Log(title = "启动虚拟机")
    @Operation(summary = "启动虚拟机")
    @PostMapping("/start")
    public List<VmwareErrorVO> startVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.startVmware(vmwareIds);
    }

    @Log(title = "关闭虚拟机")
    @Operation(summary = "关闭虚拟机")
    @PostMapping("/shutdown")
    public List<VmwareErrorVO> shutdownVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.shutdownVmware(vmwareIds);
    }

    @Log(title = "强制关闭虚拟机")
    @Operation(summary = "强制关闭虚拟机")
    @PostMapping("/destroy")
    public List<VmwareErrorVO> destroyVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.destroyVmware(vmwareIds);
    }

    @Log(title = "挂起虚拟机")
    @Operation(summary = "挂起虚拟机")
    @PostMapping("/suspend")
    public List<VmwareErrorVO> suspendVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.suspendVmware(vmwareIds);
    }

    @Log(title = "恢复虚拟机")
    @Operation(summary = "恢复虚拟机")
    @PostMapping("/resume")
    public List<VmwareErrorVO> resumeVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.resumeVmware(vmwareIds);
    }

    @Log(title = "调整虚拟机内存大小", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "调整虚拟机内存大小")
    @PutMapping("/modifyMemory")
    public void modifyVmwareMemory(
            @Parameter(description = "虚拟机ID", required = true) @RequestParam("vmwareId") Long vmwareId,
            @Parameter(description = "内存大小(字节)", required = true) @RequestParam("memory") Long memory
    ) {
        vmwareInfoService.modifyVmwareMemory(vmwareId, memory);
    }

    @Log(title = "调整虚拟机虚拟CPU数量", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "调整虚拟机虚拟CPU数量")
    @PutMapping("/modifyVcpu")
    public void modifyVmwareVcpuNumber(
            @Parameter(description = "虚拟机ID", required = true) @RequestParam("vmwareId") Long vmwareId,
            @Parameter(description = "虚拟CPU数量", required = true) @RequestParam("vcpuNumber") Integer vcpuNumber
    ) {
        vmwareInfoService.modifyVmwareVcpuNumber(vmwareId, vcpuNumber);
    }
}
