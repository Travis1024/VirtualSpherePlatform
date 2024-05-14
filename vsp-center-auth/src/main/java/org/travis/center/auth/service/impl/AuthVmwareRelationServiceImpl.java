package org.travis.center.auth.service.impl;

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
import org.travis.center.auth.pojo.dto.AuthVmwareDeleteDTO;
import org.travis.center.auth.pojo.dto.AuthVmwareInsertDTO;
import org.travis.center.auth.service.UserService;
import org.travis.center.common.entity.auth.AuthVmwareRelation;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.mapper.auth.AuthVmwareRelationMapper;
import org.travis.center.auth.service.AuthVmwareRelationService;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.UserThreadLocalUtil;

import javax.annotation.Resource;

/**
 * @ClassName AuthVmwareRelationServiceImpl
 * @Description AuthVmwareRelationServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class AuthVmwareRelationServiceImpl extends ServiceImpl<AuthVmwareRelationMapper, AuthVmwareRelation> implements AuthVmwareRelationService{
    @Resource
    private UserService userService;
    @Resource
    @Lazy
    private AuthVmwareRelationService authVmwareRelationService;
    @Resource
    private VmwareInfoMapper vmwareInfoMapper;

    @Transactional
    @Override
    public void insertRelations(AuthVmwareInsertDTO authVmwareInsertDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userService.checkAdminUser(UserThreadLocalUtil.getUserId());
        if (!checkedAdminUser) {
            throw new ForbiddenException(BizCodeEnum.FORBIDDEN.getCode(), "无操作权限!");
        }
        // 2.循环处理并新增
        Long authGroupId = authVmwareInsertDTO.getAuthGroupId();
        List<Long> vmwareIdList = authVmwareInsertDTO.getVmwareIdList();

        List<AuthVmwareRelation> authVmwareRelationList = new ArrayList<>();
        for (Long vmwareId : vmwareIdList) {
            AuthVmwareRelation authVmwareRelation = new AuthVmwareRelation();
            authVmwareRelation.setId(SnowflakeIdUtil.nextId());
            authVmwareRelation.setVmwareId(vmwareId);
            authVmwareRelation.setAuthGroupId(authGroupId);
            authVmwareRelationList.add(authVmwareRelation);
        }
        if (!authVmwareRelationList.isEmpty()) {
            authVmwareRelationService.saveBatch(authVmwareRelationList);
        }
    }

    @Override
    public void deleteRelations(AuthVmwareDeleteDTO authVmwareDeleteDTO) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userService.checkAdminUser(UserThreadLocalUtil.getUserId());
        if (!checkedAdminUser) {
            throw new ForbiddenException(BizCodeEnum.FORBIDDEN.getCode(), "无操作权限!");
        }
        // 2.处理数据库信息
        Long authGroupId = authVmwareDeleteDTO.getAuthGroupId();
        List<Long> vmwareIdList = authVmwareDeleteDTO.getVmwareIdList();
        remove(Wrappers.<AuthVmwareRelation>lambdaQuery().eq(AuthVmwareRelation::getAuthGroupId, authGroupId).in(AuthVmwareRelation::getVmwareId, vmwareIdList));
    }

    @Override
    public List<VmwareInfo> queryVmwareListByAuthGroup(Long authGroupId) {
        // 1.校验当前登录用户是否为管理员
        boolean checkedAdminUser = userService.checkAdminUser(UserThreadLocalUtil.getUserId());
        if (!checkedAdminUser) {
            throw new ForbiddenException(BizCodeEnum.FORBIDDEN.getCode(), "无操作权限!");
        }
        // 2.查询数据库信息
        List<VmwareInfo> vmwareInfoList = new ArrayList<>();
        Optional<List<AuthVmwareRelation>> optionalAuthVmwareRelations = Optional.ofNullable(getBaseMapper().selectList(Wrappers.<AuthVmwareRelation>lambdaQuery().select(AuthVmwareRelation::getVmwareId).eq(AuthVmwareRelation::getAuthGroupId, authGroupId)));
        if (optionalAuthVmwareRelations.isPresent() && !optionalAuthVmwareRelations.get().isEmpty()) {
            List<Long> vmwareIdList = optionalAuthVmwareRelations.get().stream().map(AuthVmwareRelation::getVmwareId).collect(Collectors.toList());
            vmwareInfoList = vmwareInfoMapper.selectList(Wrappers.<VmwareInfo>lambdaQuery().in(VmwareInfo::getId, vmwareIdList));
        }
        return vmwareInfoList;
    }
}
