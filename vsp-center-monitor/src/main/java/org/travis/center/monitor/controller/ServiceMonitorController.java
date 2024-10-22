package org.travis.center.monitor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.monitor.ServiceMonitor;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.monitor.pojo.dto.AddServiceMonitorDTO;
import org.travis.center.monitor.pojo.dto.ManualServiceReplaceDTO;
import org.travis.center.monitor.pojo.dto.UpdateServiceMonitorDTO;
import org.travis.center.monitor.pojo.vo.QueryServiceListVO;
import org.travis.center.monitor.service.ServiceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.aspect.RequestLock;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.enums.MachineTypeEnum;

import javax.annotation.Resource;
import java.util.List;

/**
* 服务质量监控记录表(VSP.VSP_SERVICE_MONITOR)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/monitor/service")
public class ServiceMonitorController {

    @Resource
    private ServiceMonitorService serviceMonitorService;

    @Log(title = "查询服务监控列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询服务监控列表")
    @GetMapping("/query")
    public List<ServiceMonitor> queryInfoList() {
        return serviceMonitorService.queryInfoList();
    }

    @Log(title = "分页查询服务监控列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询服务监控列表")
    @PostMapping("/pageQuery")
    public PageResult<ServiceMonitor> pageQueryInfoList(@Validated @RequestBody PageQuery pageQuery) {
        return serviceMonitorService.pageQueryInfoList(pageQuery);
    }

    @Log(title = "查询某节点运行状态的服务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询某节点运行状态的服务列表")
    @GetMapping("/query/running")
    public List<QueryServiceListVO> queryRunningServiceList(
            @Parameter(description = "1-宿主机、2-虚拟机") @RequestParam("machineType") MachineTypeEnum machineType,
            @Parameter(description = "节点UUID") @RequestParam("machineUuid") String machineUuid
    ) {
        return serviceMonitorService.queryRunningServiceList(machineType, machineUuid);
    }

    @Log(title = "查询所有状态的服务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询所有状态的服务列表")
    @GetMapping("/query/all")
    public List<QueryServiceListVO> queryAllStateServiceList(
            @Parameter(description = "1-宿主机、2-虚拟机") @RequestParam("machineType") MachineTypeEnum machineType,
            @Parameter(description = "节点UUID") @RequestParam("machineUuid") String machineUuid
    ) {
        return serviceMonitorService.queryAllStateServiceList(machineType, machineUuid);
    }

    @Log(title = "新增单个服务监控", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "新增单个服务监控")
    @PostMapping("/add")
    public ServiceMonitor addServiceMonitor(@Validated @RequestBody AddServiceMonitorDTO addServiceMonitorDTO) {
        return serviceMonitorService.addServiceMonitor(addServiceMonitorDTO);
    }

    @RequestLock
    @Log(title = "启动某个服务监控", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "启动某个服务监控")
    @PostMapping("/enable")
    public void startServiceMonitor(@RequestLockKey @Parameter(description = "服务监控ID") @RequestParam("id") Long id) {
        serviceMonitorService.startServiceMonitor(id);
    }

    @RequestLock
    @Log(title = "停止某个服务监控", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "停止某个服务监控")
    @PostMapping("/disable")
    public void stopServiceMonitor(@RequestLockKey @Parameter(description = "服务监控ID") @RequestParam("id") Long id) {
        serviceMonitorService.stopServiceMonitor(id);
    }

    @RequestLock
    @Log(title = "删除某个服务监控", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除某个服务监控")
    @PostMapping("/delete")
    public void deleteServiceMonitor(@RequestLockKey @Parameter(description = "服务监控ID") @RequestParam("id") Long id) {
        serviceMonitorService.deleteServiceMonitor(id);
    }

    @RequestLock
    @Log(title = "更新某个服务监控", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "更新某个服务监控")
    @PostMapping("/update")
    public void updateServiceMonitor(@Validated @RequestBody UpdateServiceMonitorDTO updateServiceMonitorDTO) {
        serviceMonitorService.updateServiceMonitor(updateServiceMonitorDTO);
    }

    @RequestLock
    @Log(title = "手动服务替换", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "手动服务替换")
    @PostMapping("/manualReplace")
    public void manualReplaceService(@Validated @RequestBody ManualServiceReplaceDTO manualServiceReplaceDTO) {
        serviceMonitorService.manualReplaceService(manualServiceReplaceDTO);
    }
}
