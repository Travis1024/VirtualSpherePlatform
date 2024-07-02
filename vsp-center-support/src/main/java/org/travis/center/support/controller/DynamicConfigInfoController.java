package org.travis.center.support.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.common.enums.DynamicConfigTypeEnum;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.pojo.dto.DynamicConfigUpdateDTO;
import org.travis.center.support.pojo.vo.MonitorPeriodEnumVO;
import org.travis.center.support.processor.AbstractDynamicConfigHolder;
import org.travis.center.support.service.DynamicConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.enums.MonitorPeriodEnum;

import javax.annotation.Resource;
import java.util.List;

/**
* (VSP.VSP_DYNAMIC_CONFIG_INFO)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/dynamic")
public class DynamicConfigInfoController {

    @Resource
    public DynamicConfigInfoService dynamicConfigInfoService;

    @Log(title = "查询动态配置信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询动态配置信息列表")
    @GetMapping("/select")
    public List<DynamicConfigInfo> selectList() {
        return dynamicConfigInfoService.selectList();
    }

    @Log(title = "分页查询动态配置信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询动态配置信息列表")
    @GetMapping("/pageSelect")
    public PageResult<DynamicConfigInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return dynamicConfigInfoService.pageSelectList(pageQuery);
    }

    @Log(title = "修改动态配置VALUE", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "修改动态配置VALUE")
    @PutMapping("/update")
    public void updateConfigValue(@Validated @RequestBody DynamicConfigUpdateDTO dynamicConfigUpdateDTO) {
        dynamicConfigInfoService.updateConfigValue(dynamicConfigUpdateDTO);
    }

    @Log(title = "查询[监测周期]配置可选择值", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询[监测周期]配置可选择值")
    @GetMapping("/monitorPeriodSelectableList")
    public List<MonitorPeriodEnumVO> queryMonitorPeriodSelectableList() {
        return dynamicConfigInfoService.queryMonitorPeriodSelectableList();
    }

    @Log(title = "「测试使用」查询缓存中动态配置VALUE", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "「测试使用」查询缓存中动态配置VALUE")
    @GetMapping("/selectCacheValue")
    public String selectCacheValue(@RequestParam("configId") Long configId) {
        return AbstractDynamicConfigHolder.getDynamicConfigHandler(DynamicConfigTypeEnum.UNIVERSAL).executeQueryValue(configId);
    }
}
