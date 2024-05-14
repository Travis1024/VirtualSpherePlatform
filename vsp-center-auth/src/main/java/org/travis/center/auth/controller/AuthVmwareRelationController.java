package org.travis.center.auth.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.auth.pojo.dto.AuthVmwareDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthVmwareInsertDTO;
import org.travis.center.auth.service.AuthVmwareRelationService;
import org.travis.center.common.entity.auth.AuthVmwareRelation;
import org.travis.center.auth.service.impl.AuthVmwareRelationServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.travis.center.common.entity.manage.VmwareInfo;

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

    @Operation(summary = "新增虚拟机-权限组关系")
    @PostMapping("/insert")
    private void insertRelations(@Validated @RequestBody AuthVmwareInsertDTO authVmwareInsertDTO) {
        authVmwareRelationService.insertRelations(authVmwareInsertDTO);
    }

    @Operation(summary = "删除虚拟机-权限组关系")
    @DeleteMapping("/delete")
    private void deleteRelations(@Validated @RequestBody AuthVmwareDeleteDTO authVmwareDeleteDTO) {
        authVmwareRelationService.deleteRelations(authVmwareDeleteDTO);
    }

    @Operation(summary = "查询权限组所关联的虚拟机信息列表")
    @GetMapping("/queryVmByGroupId")
    private List<VmwareInfo> queryVmwareListByAuthGroup(@RequestParam("authGroupId") Long authGroupId) {
        return authVmwareRelationService.queryVmwareListByAuthGroup(authGroupId);
    }
}
