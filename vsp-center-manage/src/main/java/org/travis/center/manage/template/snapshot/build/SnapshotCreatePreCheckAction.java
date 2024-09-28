package org.travis.center.manage.template.snapshot.build;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.enums.DiskMountEnum;
import org.travis.center.common.mapper.manage.DiskInfoMapper;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.manage.pojo.pipeline.SnapshotInsertPipe;
import org.travis.shared.common.constants.DiskConstant;
import org.travis.shared.common.constants.SnapshotConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.PipelineProcessException;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName SnapshotPreCheckAction
 * @Description 快照创建预检查动作
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Service
public class SnapshotCreatePreCheckAction implements BusinessExecutor<SnapshotInsertPipe> {

    @Resource
    private DiskInfoMapper diskInfoMapper;
    @Resource
    private SnapshotInfoMapper snapshotInfoMapper;

    @Transactional
    @Override
    public void execute(ProcessContext<SnapshotInsertPipe> context) {

        SnapshotInsertPipe dataModel = context.getDataModel();

        // 1.查询虚拟机所有挂载的磁盘列表
        List<DiskInfo> diskInfoList = diskInfoMapper.selectList(
                Wrappers.<DiskInfo>lambdaQuery()
                        .eq(DiskInfo::getVmwareId, dataModel.getVmwareId())
                        .eq(DiskInfo::getIsMount, DiskMountEnum.MOUNTED)
        );
        if (diskInfoList == null || diskInfoList.isEmpty()) {
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(),"虚拟机未挂载任何磁盘，无法创建快照！"));
            context.setNeedBreak(true);
            return;
        }

        // 2.查询当前虚拟机的所有历史快照版本
        Optional.ofNullable(snapshotInfoMapper.selectList(
                Wrappers.<SnapshotInfo>lambdaQuery().eq(SnapshotInfo::getVmwareId, dataModel.getVmwareId())
        )).ifPresent(dataModel::setHistorySnapshotInfoList);

        // 3..补充责任链上下文数据模型
        diskInfoList.forEach(diskInfo -> {
            // 3.1.循环初始化 Snapshot 对象
            SnapshotInfo snapshotInfo = new SnapshotInfo();
            snapshotInfo.setId(SnowflakeIdUtil.nextId());
            snapshotInfo.setSnapshotName(dataModel.getSnapshotName());
            snapshotInfo.setVmwareId(dataModel.getVmwareId());
            snapshotInfo.setVmwareUuid(dataModel.getVmwareUuid());
            snapshotInfo.setDescription(dataModel.getDescription());
            snapshotInfo.setAutoSnapshotName(SnapshotConstant.AUTO_SNAPSHOT_NAME);

            String subPath = diskInfo.getSubPath();
            Assert.isTrue(StrUtil.isNotBlank(subPath), () -> new PipelineProcessException("磁盘挂载路径为空: " + JSONUtil.toJsonStr(diskInfo)));
            Assert.isTrue(subPath.trim().endsWith(DiskConstant.DISK_NAME_SUFFIX), () -> new PipelineProcessException("磁盘挂载路径不合法: " + JSONUtil.toJsonStr(diskInfo)));
            subPath = subPath.substring(0, subPath.length() - DiskConstant.DISK_NAME_SUFFIX.length()) + StrUtil.DOT + SnapshotConstant.AUTO_SNAPSHOT_NAME;
            snapshotInfo.setSubPath(subPath);

            snapshotInfo.setTargetDev(diskInfo.getTargetDev());

            // 3.2.补充上下文数据模型
            dataModel.getLatestSnapshotInfoList().add(snapshotInfo);
        });
    }
}
