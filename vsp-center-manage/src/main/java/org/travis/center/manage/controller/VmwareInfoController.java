package org.travis.center.manage.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.*;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.aspect.RequestLock;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    @PostMapping("/pageSelect")
    public PageResult<VmwareInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return vmwareInfoService.pageSelectList(pageQuery);
    }

    @RequestLock(expire = 60)
    @Log(title = "创建虚拟机", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "创建虚拟机")
    @PostMapping("/create")
    public void createVmwareInfo(@Validated @RequestBody VmwareInsertDTO vmwareInsertDTO) throws IOException {
        vmwareInfoService.createVmwareInfo(vmwareInsertDTO);
    }

    @RequestLock(expire = 30)
    @Log(title = "删除虚拟机", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除虚拟机")
    @DeleteMapping("/delete")
    public List<VmwareErrorVO> deleteVmware(@RequestLockKey @RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.deleteVmware(vmwareIds);
    }

    @RequestLock(expire = 30)
    @Log(title = "启动虚拟机")
    @Operation(summary = "启动虚拟机")
    @PostMapping("/start")
    public List<VmwareErrorVO> startVmware(@RequestLockKey @RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.startVmware(vmwareIds);
    }

    @RequestLock(expire = 30)
    @Log(title = "关闭虚拟机")
    @Operation(summary = "关闭虚拟机")
    @PostMapping("/shutdown")
    public List<VmwareErrorVO> shutdownVmware(@RequestLockKey @RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.shutdownVmware(vmwareIds);
    }

    @RequestLock(expire = 30)
    @Log(title = "强制关闭虚拟机")
    @Operation(summary = "强制关闭虚拟机")
    @PostMapping("/destroy")
    public List<VmwareErrorVO> destroyVmware(@RequestLockKey @RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.destroyVmware(vmwareIds);
    }

    @RequestLock(expire = 30)
    @Log(title = "挂起虚拟机")
    @Operation(summary = "挂起虚拟机")
    @PostMapping("/suspend")
    public List<VmwareErrorVO> suspendVmware(@RequestLockKey @RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.suspendVmware(vmwareIds);
    }

    @RequestLock(expire = 30)
    @Log(title = "恢复虚拟机")
    @Operation(summary = "恢复虚拟机")
    @PostMapping("/resume")
    public List<VmwareErrorVO> resumeVmware(@RequestLockKey @RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.resumeVmware(vmwareIds);
    }

    @RequestLock(expire = 60)
    @Log(title = "调整虚拟机内存大小", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "调整虚拟机内存大小")
    @PutMapping("/modifyMemory")
    public void modifyVmwareMemory(
            @RequestLockKey @Parameter(description = "虚拟机ID", required = true) @RequestParam("vmwareId") Long vmwareId,
            @Parameter(description = "内存大小(字节)", required = true) @RequestParam("memory") Long memory
    ) {
        vmwareInfoService.modifyVmwareMemory(vmwareId, memory);
    }

    @RequestLock(expire = 60)
    @Log(title = "调整虚拟机虚拟CPU数量", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "调整虚拟机虚拟CPU数量")
    @PutMapping("/modifyVcpu")
    public void modifyVmwareVcpuNumber(
            @RequestLockKey @Parameter(description = "虚拟机ID", required = true) @RequestParam("vmwareId") Long vmwareId,
            @Parameter(description = "虚拟CPU数量", required = true) @RequestParam("vcpuNumber") Integer vcpuNumber
    ) {
        vmwareInfoService.modifyVmwareVcpuNumber(vmwareId, vcpuNumber);
    }

    @Log(title = "获取虚拟机VNC地址", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "获取虚拟机VNC地址")
    @GetMapping("/vncAddr")
    public String queryVncAddress(@RequestParam("vmwareId") Long vmwareId) {
        return vmwareInfoService.queryVncAddress(vmwareId);
    }

    @Log(title = "查询虚拟机IP地址-(扫描)", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询虚拟机IP地址-(扫描)")
    @GetMapping("/ipAddr")
    public String queryIpAddress(@RequestParam("vmwareId") Long vmwareId) {
        return vmwareInfoService.queryIpAddress(vmwareId);
    }

    @Log(title = "查询虚拟机IP地址-(Agent)", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询虚拟机IP地址-(Agent)")
    @GetMapping("/ipAddrAgent")
    public String queryIpAddressByAgent(@RequestParam("vmwareId") Long vmwareId) {
        return vmwareInfoService.queryIpAddressByAgent(vmwareId);
    }

    @Log(title = "批量查询虚拟机IP地址-(扫描)", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "批量查询虚拟机IP地址-(扫描)")
    @GetMapping("/batchIpAddr")
    public Map<Long, String> batchQueryIpAddress(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.batchQueryIpAddress(vmwareIds);
    }

    @Log(title = "批量查询虚拟机IP地址-(Agent)", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "批量查询虚拟机IP地址-(Agent)")
    @GetMapping("/batchIpAddrAgent")
    public Map<Long, String> batchQueryIpAddressByAgent(@RequestParam("vmwareIds") List<Long> vmwareIds) {
        return vmwareInfoService.batchQueryIpAddressByAgent(vmwareIds);
    }

    @Log(title = "核对虚拟机状态信息")
    @Operation(summary = "核对虚拟机状态信息")
    @GetMapping("/correct")
    public void correctVmwareState() {
        vmwareInfoService.correctVmwareState();
    }

    @Log(title = "虚拟机热迁移")
    @Operation(summary = "虚拟机热迁移")
    @PostMapping("/liveMigrate")
    public String liveMigrate(@Validated @RequestBody VmwareMigrateDTO vmwareMigrateDTO) {
        return vmwareInfoService.liveMigrate(vmwareMigrateDTO);
    }

    @Log(title = "虚拟机冷迁移")
    @Operation(summary = "虚拟机冷迁移")
    @PostMapping("/offlineMigrate")
    public String offlineMigrate(@Validated @RequestBody VmwareMigrateDTO vmwareMigrateDTO) {
        return vmwareInfoService.offlineMigrate(vmwareMigrateDTO);
    }

    @RequestLock
    @Log(title = "设置虚拟机管理员登录信息", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "设置虚拟机管理员登录信息")
    @PutMapping("/setLoginInfo")
    public void setLoginInfo(@Validated @RequestBody VmwareLoginInfoUpdateDTO vmwareLoginInfoUpdateDTO) {
        vmwareInfoService.setLoginInfo(vmwareLoginInfoUpdateDTO);
    }

    @Log(title = "虚拟机SSH连接预检测")
    @Operation(summary = "虚拟机SSH连接预检测")
    @PostMapping("/sshPreCheck")
    public boolean validateHostSshConnect(@Validated @RequestBody VmwareSshCheckDTO vmwareSshCheckDTO) {
        return vmwareInfoService.validateVmwareSshConnect(
                vmwareSshCheckDTO.getVmwareId(),
                vmwareSshCheckDTO.getUsername(),
                vmwareSshCheckDTO.getPassword()
        );
    }
}
