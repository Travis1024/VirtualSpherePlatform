package org.travis.center.auth.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.auth.pojo.dto.AuthGroupInsertDTO;
import org.travis.center.auth.pojo.dto.AuthGroupUpdateDTO;
import org.travis.center.common.entity.auth.AuthGroup;
import org.travis.center.common.entity.auth.AuthUserRelation;
import org.travis.center.common.mapper.auth.AuthGroupMapper;
import org.travis.center.auth.service.AuthGroupService;
import org.travis.center.common.mapper.auth.AuthUserRelationMapper;
import org.travis.center.common.service.UserAssistService;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.UserThreadLocalUtil;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;

/**
 * @ClassName AuthGroupServiceImpl
 * @Description AuthGroupServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class AuthGroupServiceImpl extends ServiceImpl<AuthGroupMapper, AuthGroup> implements AuthGroupService{
    @Resource
    public UserAssistService userAssistService;
    @Resource
    public AuthUserRelationMapper authUserRelationMapper;

    @Override
    public List<AuthGroup> queryCurrentUserAuthGroup() {
        List<AuthGroup> authGroupList = new ArrayList<>();
        // 判断当前用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        if (checkedAdminUser) {
            // 管理员返回所有权限组列表
            authGroupList = getBaseMapper().selectList(null);
        } else {
            // 普通用户返回自己所具有的权限组列表
            Optional<List<AuthUserRelation>> optionalAuthUserRelations = Optional.ofNullable(authUserRelationMapper.selectList(Wrappers.<AuthUserRelation>lambdaQuery().eq(AuthUserRelation::getUserId, UserThreadLocalUtil.getUserId())));
            if (optionalAuthUserRelations.isPresent()) {
                List<Long> authGroupIdList = optionalAuthUserRelations.get().stream().map(AuthUserRelation::getAuthGroupId).collect(Collectors.toList());
                authGroupList = getBaseMapper().selectList(Wrappers.<AuthGroup>lambdaQuery().in(AuthGroup::getId, authGroupIdList));
            }
        }
        return authGroupList;
    }

    @Override
    public AuthGroup insertOneAuthGroup(AuthGroupInsertDTO authGroupInsertDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, ForbiddenException::new);
        // 2.存储权限组信息
        AuthGroup authGroup = new AuthGroup();
        BeanUtils.copyProperties(authGroupInsertDTO, authGroup);
        authGroup.setId(SnowflakeIdUtil.nextId());
        VspStrUtil.trimStr(authGroup);
        save(authGroup);
        return authGroup;
    }

    @Override
    public void updateOneAuthGroup(AuthGroupUpdateDTO authGroupUpdateDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, ForbiddenException::new);
        // 2.修改权限组信息
        update(
                Wrappers.<AuthGroup>lambdaUpdate()
                        .set(StrUtil.isNotEmpty(authGroupUpdateDTO.getDescription()), AuthGroup::getDescription, authGroupUpdateDTO.getDescription())
                        .eq(AuthGroup::getId, authGroupUpdateDTO.getId())
        );
    }
}
