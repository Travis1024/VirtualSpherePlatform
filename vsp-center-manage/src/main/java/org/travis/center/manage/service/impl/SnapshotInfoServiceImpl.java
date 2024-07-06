package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.PipelineBusinessCodeEnum;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.manage.pojo.dto.SnapshotInsertDTO;
import org.travis.center.manage.pojo.pipe.SnapshotInsertPipe;
import org.travis.center.manage.service.SnapshotInfoService;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.LockConflictException;
import org.travis.shared.common.exceptions.PipelineProcessException;
import org.travis.shared.common.pipeline.FlowController;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;

/**
 * @ClassName SnapshotInfoServiceImpl
 * @Description SnapshotInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/5
 */
@Service
public class SnapshotInfoServiceImpl extends ServiceImpl<SnapshotInfoMapper, SnapshotInfo> implements SnapshotInfoService{

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private VmwareInfoMapper vmwareInfoMapper;
    @Resource(name = "resourceFlowController")
    private FlowController resourceFlowController;

    @Override
    public List<SnapshotInfo> selectSnapshotList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<SnapshotInfo> pageSelectSnapshotList(PageQuery pageQuery) {
        Page<SnapshotInfo> snapshotInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(snapshotInfoPage);
    }

    @Override
    public void createSnapshotInfo(SnapshotInsertDTO snapshotInsertDTO) {

        // TODO 改为异步创建, 全局推送通知
        RLock rLock = redissonClient.getLock(LockConstant.LOCK_VMWARE_PREFIX + snapshotInsertDTO.getVmwareId());

        try {
            // 1.根据虚拟机 ID 加锁, 尝试拿锁
            Assert.isTrue(rLock.tryLock(120, TimeUnit.SECONDS), () -> new LockConflictException(BizCodeEnum.LOCKED.getCode(), "虚拟机正在操作中，请稍后重试!"));

            // 2.查询虚拟机 UUID 信息
            VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(
                    Wrappers.<VmwareInfo>lambdaQuery()
                            .select(VmwareInfo::getId, VmwareInfo::getUuid)
                            .eq(VmwareInfo::getId, snapshotInsertDTO.getVmwareId())
            )).orElseThrow(() -> new CommonException(BizCodeEnum.NOT_FOUND.getCode(), "虚拟机信息查询失败!"));

            /**
             * 3.封装责任链上下文数据模型
             */
            SnapshotInsertPipe snapshotInsertPipe = SnapshotInsertPipe.builder()
                    .snapshotName(snapshotInsertDTO.getSnapshotName())
                    .vmwareId(snapshotInsertDTO.getVmwareId())
                    .vmwareUuid(vmwareInfo.getUuid())
                    .description(snapshotInsertDTO.getDescription())
                    .snapshotInfoList(new ArrayList<>())
                    .build();

            /**
             * 4.封装执行的责任链上下文
             */
            ProcessContext<?> processContext = ProcessContext.builder()
                    .businessCode(PipelineBusinessCodeEnum.SNAPSHOT_CREATE.getCode())
                    .dataModel(snapshotInsertPipe)
                    .needBreak(false)
                    .response(R.ok())
                    .build();

            /**
             * 5.责任链流执行器执行
             */
            ProcessContext<?> resultContext = resourceFlowController.process(processContext);
            Assert.isTrue(resultContext.checkSuccess(), () -> new PipelineProcessException(resultContext.getResponse().getCode(), resultContext.getResponse().getMsg()));

        }catch (CommonException commonException) {
            log.error(commonException.getMessage(), commonException);
            throw commonException;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
