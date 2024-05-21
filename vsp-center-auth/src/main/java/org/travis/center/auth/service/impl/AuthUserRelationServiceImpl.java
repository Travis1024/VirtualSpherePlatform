package org.travis.center.auth.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.auth.pojo.dto.AuthUserDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthUserInsertDTO;
import org.travis.center.auth.service.UserService;
import org.travis.center.common.entity.auth.AuthUserRelation;
import org.travis.center.common.entity.auth.User;
import org.travis.center.common.mapper.auth.AuthUserRelationMapper;
import org.travis.center.auth.service.AuthUserRelationService;
import org.travis.center.common.service.UserAssistService;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.UserThreadLocalUtil;

import javax.annotation.Resource;

/**
 * @ClassName AuthUserRelationServiceImpl
 * @Description AuthUserRelationServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class AuthUserRelationServiceImpl extends ServiceImpl<AuthUserRelationMapper, AuthUserRelation> implements AuthUserRelationService{
    @Resource
    private UserService userService;
    @Resource
    private UserAssistService userAssistService;
    @Resource
    @Lazy
    private AuthUserRelationService authUserRelationService;

    @Transactional
    @Override
    public void insertRelations(AuthUserInsertDTO authUserInsertDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, () -> new ForbiddenException(BizCodeEnum.FORBIDDEN.getCode(), "无操作权限!"));
        // 2.循环处理并新增
        Long authGroupId = authUserInsertDTO.getAuthGroupId();
        List<Long> userIdList = authUserInsertDTO.getUserIdList();

        List<AuthUserRelation> authUserRelationList = new ArrayList<>();
        for (Long userId : userIdList) {
            AuthUserRelation authUserRelation = new AuthUserRelation();
            authUserRelation.setId(SnowflakeIdUtil.nextId());
            authUserRelation.setUserId(userId);
            authUserRelation.setAuthGroupId(authGroupId);
            authUserRelationList.add(authUserRelation);
        }
        if (!authUserRelationList.isEmpty()) {
            authUserRelationService.saveBatch(authUserRelationList);
        }
    }

    @Override
    public void deleteRelations(AuthUserDeleteDTO authUserDeleteDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, () -> new ForbiddenException(BizCodeEnum.FORBIDDEN.getCode(), "无操作权限!"));
        // 2.处理数据库信息
        Long authGroupId = authUserDeleteDTO.getAuthGroupId();
        List<Long> userIdList = authUserDeleteDTO.getUserIdList();
        remove(Wrappers.<AuthUserRelation>lambdaQuery().eq(AuthUserRelation::getAuthGroupId, authGroupId).in(AuthUserRelation::getUserId, userIdList));
    }

    @Override
    public List<User> queryUserListByAuthGroup(Long authGroupId) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userAssistService.checkAdminUser();
        Assert.isTrue(checkedAdminUser, () -> new ForbiddenException(BizCodeEnum.FORBIDDEN.getCode(), "无操作权限!"));
        // 2.查询数据库信息
        List<User> userList = new ArrayList<>();
        Optional<List<AuthUserRelation>> optionalAuthUserRelations = Optional.ofNullable(getBaseMapper().selectList(Wrappers.<AuthUserRelation>lambdaQuery().select(AuthUserRelation::getUserId).eq(AuthUserRelation::getAuthGroupId, authGroupId)));
        if (optionalAuthUserRelations.isPresent() && !optionalAuthUserRelations.get().isEmpty()) {
            List<Long> userIdList = optionalAuthUserRelations.get().stream().map(AuthUserRelation::getUserId).collect(Collectors.toList());
            userList = userService.list(Wrappers.<User>lambdaQuery().in(User::getId, userIdList));
        }
        return userList;
    }
}
