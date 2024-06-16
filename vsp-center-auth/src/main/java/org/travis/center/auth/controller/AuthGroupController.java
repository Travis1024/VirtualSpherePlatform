package org.travis.center.auth.controller;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.travis.center.auth.pojo.dto.AuthGroupInsertDTO;
import org.travis.center.auth.pojo.dto.AuthGroupUpdateDTO;
import org.travis.center.auth.service.AuthGroupService;
import org.travis.center.common.entity.auth.AuthGroup;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.support.aspect.Log;

import javax.annotation.Resource;
import java.util.List;

/**
* 权限组信息表(VSP.VSP_AUTH_GROUP)表控制层
*
* @author xxxxx
*/
@Slf4j
@RestController
@RequestMapping("/authGroup")
public class AuthGroupController {
    @Resource
    public AuthGroupService authGroupService;

    @Log(title = "查询当前用户所关联权限组列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询当前用户所关联权限组列表")
    @GetMapping("/queryCurUser")
    public List<AuthGroup> queryCurrentUserAuthGroup() {
        return authGroupService.queryCurrentUserAuthGroup();
    }

    @Log(title = "管理员新增权限组信息", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "管理员新增权限组信息")
    @PostMapping("/insert")
    public AuthGroup insertOneAuthGroup(@Validated @RequestBody AuthGroupInsertDTO authGroupInsertDTO) {
        return authGroupService.insertOneAuthGroup(authGroupInsertDTO);
    }

    @Log(title = "管理员更新权限组信息", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "管理员更新权限组信息")
    @PutMapping("/update")
    public void updateOneAuthGroup(@Validated @RequestBody AuthGroupUpdateDTO authGroupUpdateDTO) {
        authGroupService.updateOneAuthGroup(authGroupUpdateDTO);
    }
}
