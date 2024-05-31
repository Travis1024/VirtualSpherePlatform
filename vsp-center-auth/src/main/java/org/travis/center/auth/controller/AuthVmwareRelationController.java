package org.travis.center.auth.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.auth.pojo.dto.AuthVmwareDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthVmwareInsertDTO;
import org.travis.center.auth.service.AuthVmwareRelationService;
import org.springframework.web.bind.annotation.*;

import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.message.aspect.Log;

import javax.annotation.Resource;
import java.util.List;

/**
* 权限组-虚拟机关联关系表(VSP.VSP_AUTH_VMWARE_RELATION)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/authVmware")
public class AuthVmwareRelationController {
    @Resource
    private AuthVmwareRelationService authVmwareRelationService;

    @Log(title = "新增虚拟机-权限组关系", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "新增虚拟机-权限组关系")
    @PostMapping("/insert")
    public void insertRelations(@Validated @RequestBody AuthVmwareInsertDTO authVmwareInsertDTO) {
        authVmwareRelationService.insertRelations(authVmwareInsertDTO);
    }

    @Log(title = "删除虚拟机-权限组关系", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除虚拟机-权限组关系")
    @DeleteMapping("/delete")
    public void deleteRelations(@Validated @RequestBody AuthVmwareDeleteDTO authVmwareDeleteDTO) {
        authVmwareRelationService.deleteRelations(authVmwareDeleteDTO);
    }

    @Log(title = "查询权限组所关联的虚拟机信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询权限组所关联的虚拟机信息列表")
    @GetMapping("/queryVmByGroupId")
    public List<VmwareInfo> queryVmwareListByAuthGroup(@RequestParam("authGroupId") Long authGroupId) {
        return authVmwareRelationService.queryVmwareListByAuthGroup(authGroupId);
    }
}
