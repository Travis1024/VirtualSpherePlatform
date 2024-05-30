package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.center.manage.service.VmwareInfoService;
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

    @Operation(summary = "通过主键查询单条虚拟机数据")
    @GetMapping("/selectOne")
    public VmwareInfo selectOne(@RequestParam("id") Long id) {
        return vmwareInfoService.selectOne(id);
    }

    @Operation(summary = "查询虚拟机信息列表")
    @GetMapping("/select")
    public List<VmwareInfo> selectAll() {
        return vmwareInfoService.selectAll();
    }

    @Operation(summary = "分页查询虚拟机信息列表")
    @GetMapping("/pageSelect")
    public PageResult<VmwareInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return vmwareInfoService.pageSelectList(pageQuery);
    }

    @Operation(summary = "创建虚拟机")
    @PostMapping("/create")
    public String createVmwareInfo(@Validated @RequestBody VmwareInsertDTO vmwareInsertDTO) throws IOException {
        return vmwareInfoService.createVmwareInfo(vmwareInsertDTO);
    }

    @Operation(summary = "启动虚拟机")
    @PostMapping("/start")
    public List<VmwareErrorVO> startVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.startVmware(vmwareIds);
    }

    @Operation(summary = "关闭虚拟机")
    @PostMapping("/shutdown")
    public List<VmwareErrorVO> shutdownVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.shutdownVmware(vmwareIds);
    }

    @Operation(summary = "强制关闭虚拟机")
    @PostMapping("/destroy")
    public List<VmwareErrorVO> destroyVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.destroyVmware(vmwareIds);
    }

    @Operation(summary = "挂起虚拟机")
    @PostMapping("/suspend")
    public List<VmwareErrorVO> suspendVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.suspendVmware(vmwareIds);
    }

    @Operation(summary = "恢复虚拟机")
    @PostMapping("/resume")
    public List<VmwareErrorVO> resumeVmware(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.resumeVmware(vmwareIds);
    }
}
