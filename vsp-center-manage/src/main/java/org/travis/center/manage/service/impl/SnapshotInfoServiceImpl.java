package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.manage.pojo.dto.SnapshotInsertDTO;
import org.travis.center.manage.service.SnapshotInfoService;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.LockConflictException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;

/**
 * @ClassName SnapshotInfoServiceImpl
 * @Description TODO
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
            VmwareInfo vmwareInfo = vmwareInfoMapper.selectOne(
                    Wrappers.<VmwareInfo>lambdaQuery()
                            .select(VmwareInfo::getId, VmwareInfo::getUuid)
                            .eq(VmwareInfo::getId, snapshotInsertDTO.getVmwareId())
            );

            SnapshotInfo snapshotInfo = SnapshotInfo.builder()
                    .id(SnowflakeIdUtil.nextId())
                    .snapshotName(snapshotInsertDTO.getSnapshotName())
                    .vmwareId(vmwareInfo.getId())
                    .vmwareUuid(vmwareInfo.getUuid())
                    .description(snapshotInsertDTO.getDescription())
                    .versionType()
                    .autoSnapshotName(snapshotInsertDTO.getSnapshotName())
                    .targetDev(snapshotInsertDTO.getTargetDev())
                    .vmwareUuid(snapshotInsertDTO.getVmwareUuid())
                    .description(snapshotInsertDTO.getDescription())

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
