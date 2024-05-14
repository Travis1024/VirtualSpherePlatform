package org.travis.center.auth.controller;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.travis.center.auth.pojo.dto.AuthUserDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthUserInsertDTO;
import org.travis.center.auth.service.AuthUserRelationService;
import org.travis.center.common.entity.auth.AuthUserRelation;
import org.travis.center.auth.service.impl.AuthUserRelationServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.travis.center.common.entity.auth.User;

import javax.annotation.Resource;
import java.util.List;

/**
* 权限组-用户关联关系表(VSP.VSP_AUTH_USER_RELATION)表控制层
*
* @author xxxxx
*/
@Slf4j
@RestController
@RequestMapping("/authUser")
public class AuthUserRelationController {

    @Resource
    private AuthUserRelationService authUserRelationService;

    @Operation(summary = "新增用户-权限组关系")
    @PostMapping("/insert")
    private void insertRelations(@Validated @RequestBody AuthUserInsertDTO authUserInsertDTO) {
        authUserRelationService.insertRelations(authUserInsertDTO);
    }

    @Operation(summary = "删除用户-权限组关系")
    @DeleteMapping("/delete")
    private void deleteRelations(@Validated @RequestBody AuthUserDeleteDTO authUserDeleteDTO) {
        authUserRelationService.deleteRelations(authUserDeleteDTO);
    }

    @Operation(summary = "查询权限组所关联的用户信息列表")
    @GetMapping("/queryUserByGroupId")
    private List<User> queryUserListByAuthGroup(@RequestParam("authGroupId") Long authGroupId) {
        return authUserRelationService.queryUserListByAuthGroup(authGroupId);
    }
}
