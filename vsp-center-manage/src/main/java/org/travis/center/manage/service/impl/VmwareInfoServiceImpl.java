package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentVmwareClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.manage.creation.AbstractCreationService;
import org.travis.center.manage.creation.CreationHolder;
import org.travis.center.manage.pojo.dto.VmwareInsertDTO;
import org.travis.center.manage.pojo.vo.VmwareErrorVO;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;

import javax.annotation.Resource;

/**
 * @ClassName VmwareInfoServiceImpl
 * @Description VmwareInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Slf4j
@Service
public class VmwareInfoServiceImpl extends ServiceImpl<VmwareInfoMapper, VmwareInfo> implements VmwareInfoService{

    @Resource
    private CreationHolder creationHolder;
    @Resource
    private HostInfoMapper hostInfoMapper;
    @DubboReference
    public AgentVmwareClient agentVmwareClient;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public VmwareInfo selectOne(Long id) {
        Optional<VmwareInfo> vmwareInfoOptional = Optional.ofNullable(getById(id));
        Assert.isTrue(vmwareInfoOptional.isPresent(), () -> new BadRequestException("未查询到相关虚拟机信息!"));
        return vmwareInfoOptional.get();
    }

    @Override
    public List<VmwareInfo> selectAll() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<VmwareInfo> pageSelectList(PageQuery pageQuery) {
        Page<VmwareInfo> vmwareInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(vmwareInfoPage);
    }

    @Transactional
    @Override
    public VmwareInfo createVmwareInfo(VmwareInsertDTO vmwareInsertDTO) throws IOException {
        AbstractCreationService creationService = creationHolder.getCreationService(vmwareInsertDTO.getCreateForm().getValue());
        return creationService.build(vmwareInsertDTO);
    }

    @Override
    public List<VmwareErrorVO> startVmware(List<Long> vmwareIds) {
        return commonVmwareOperation(
                vmwareIds,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.startVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    @Override
    public List<VmwareErrorVO> suspendVmware(List<Long> vmwareIds) {
        return commonVmwareOperation(
                vmwareIds,
                (vmwareInfo, hostInfo) -> new CommonOperateParams(hostInfo.getIp(), vmwareInfo.getUuid()),
                commonOperateParams -> agentVmwareClient.suspendVmware(commonOperateParams.getHostIp(), commonOperateParams.getVmwareUuid())
        );
    }

    /**
     * 复杂参数处理示例
     */
    public List<VmwareErrorVO> complexVmware(List<Long> vmwareIds) {
        return commonVmwareOperation(
                vmwareIds,
                (vmwareInfo, hostInfo) -> new SubOperateParams(hostInfo.getIp(), vmwareInfo.getUuid(), "subParam"),
                (Function<SubOperateParams, R<String>>) subOperateParams -> agentVmwareClient.complexVmware(subOperateParams.getHostIp(), subOperateParams.getVmwareUuid(), subOperateParams.getSubParam())
        );
    }


    /**
     * 虚拟机通用操作封装
     *
     * @param vmwareIds 虚拟机 ID 列表
     * @param function  虚拟机操作函数
     * @return  List<VmwareErrorVO> 错误信息列表
     */
    private <T extends CommonOperateParams> List<VmwareErrorVO> commonVmwareOperation(List<Long> vmwareIds, BiFunction<VmwareInfo, HostInfo, CommonOperateParams> functionParamsBuilder, Function<T, R<String>> function) {
        // 1.查询虚拟机相关信息
        List<VmwareInfo> vmwareInfoList = getBaseMapper().selectBatchIds(vmwareIds);
        Map<Long, HostInfo> hostInfoMap = queryHostInfoMap(vmwareInfoList);

        // 2.遍历虚拟机列表，执行相关操作
        List<VmwareErrorVO> errorInfoList = new ArrayList<>();
        for (VmwareInfo vmwareInfo : vmwareInfoList) {
            VmwareErrorVO vmwareErrorVO = singleVmwareOperate(
                    vmwareInfo.getId(),
                    functionParamsBuilder.apply(vmwareInfo, hostInfoMap.get(vmwareInfo.getHostId())),
                    function
            );

            if (vmwareErrorVO != null) {
                errorInfoList.add(vmwareErrorVO);
            }
        }
        return errorInfoList;
    }

    /**
     * 根据虚拟机信息列表查询宿主机信息
     *
     * @param vmwareInfoList    虚拟机信息列表
     * @return  [{hostId, hostInfo}]
     */
    private Map<Long, HostInfo> queryHostInfoMap(List<VmwareInfo> vmwareInfoList) {
        // <vmwareId, hostId>
        Map<Long, Long> vmHostIdMap = vmwareInfoList.stream().collect(Collectors.toMap(VmwareInfo::getId, VmwareInfo::getHostId));
        List<Long> hostIds = new ArrayList<>(vmHostIdMap.values());
        // <hostId, hostInfo>
        return hostInfoMapper.selectBatchIds(hostIds).stream().collect(Collectors.toMap(HostInfo::getId, one -> one));
    }

    @Data
    @Builder
    static class CommonOperateParams {
        public String hostIp;
        public String vmwareUuid;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Builder
    static class SubOperateParams extends CommonOperateParams{

        public SubOperateParams(String hostIp, String vmwareUuid, String subParam) {
            super(hostIp, vmwareUuid);
            this.subParam = subParam;
        }

        public String subParam;
    }

    @SuppressWarnings("unchecked")
    private <T extends CommonOperateParams> VmwareErrorVO singleVmwareOperate(Long vmwareId, CommonOperateParams operateParams, Function<T, R<String>> function) {
        VmwareErrorVO vmwareErrorVO = null;
        RLock lock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + vmwareId);
        try {
            // 1.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(lock.tryLock(0, TimeUnit.MILLISECONDS), () -> new CommonException(BizCodeEnum.LOCKED.getCode(), "虚拟机正在操作中，请稍后重试!"));

            // 2.Dubbo-操作
            R<String> vmwareOperationR = function.apply((T) operateParams);
            Assert.isTrue(vmwareOperationR.checkSuccess(), () -> new DubboFunctionException(vmwareOperationR.getMsg()));

            // 3.操作成功则打印启动日志
            log.info("{} Operation Message -> {}", vmwareId, vmwareOperationR.getData());

            return null;
        } catch (Exception e) {
            // 启动失败则封装错误消息
            vmwareErrorVO = new VmwareErrorVO();
            vmwareErrorVO.setVmwareId(vmwareId);
            vmwareErrorVO.setErrorMessage(e.getMessage());
            return vmwareErrorVO;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
