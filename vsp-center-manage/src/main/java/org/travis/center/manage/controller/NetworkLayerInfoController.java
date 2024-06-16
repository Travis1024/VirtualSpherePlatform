package org.travis.center.manage.controller;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.NetworkLayerInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.NetworkInsertDTO;
import org.travis.center.manage.service.NetworkLayerInfoService;
import org.travis.center.support.aspect.Log;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.BadRequestException;

import javax.annotation.Resource;
import java.util.List;

/**
* (VSP.VSP_NETWORK_LAYER_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/networkLayer")
public class NetworkLayerInfoController {
    @Resource
    public NetworkLayerInfoService networkLayerInfoService;

    @Log(title = "新增二层网络信息", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "新增二层网络信息")
    @PostMapping("/insertOne")
    public NetworkLayerInfo insertOne(@Validated @RequestBody NetworkInsertDTO networkInsertDTO) {
        Assert.isTrue(Validator.isIpv4(networkInsertDTO.getNicStartAddress()), () -> new BadRequestException("网卡起始 IP 地址校验失败!"));
        return networkLayerInfoService.insertOne(networkInsertDTO);
    }

    @Log(title = "删除二层网络信息", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除二层网络信息")
    @DeleteMapping("/deleteOne")
    public void deleteOne(@RequestParam("networkLayerId") Long networkLayerId) {
        networkLayerInfoService.deleteOne(networkLayerId);
    }

    @Log(title = "查询二层网络信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询二层网络信息列表")
    @GetMapping("/select")
    public List<NetworkLayerInfo> selectList() {
        return networkLayerInfoService.selectList();
    }

    @Log(title = "分页查询二层网络信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询二层网络信息列表")
    @PostMapping("/pageSelect")
    public PageResult<NetworkLayerInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return networkLayerInfoService.pageSelectList(pageQuery);
    }
}
