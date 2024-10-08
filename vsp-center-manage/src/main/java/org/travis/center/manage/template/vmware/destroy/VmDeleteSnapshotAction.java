package org.travis.center.manage.template.vmware.destroy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.mapper.manage.SnapshotInfoMapper;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.pipeline.VmwareDestroyPipe;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.PipelineProcessException;
import org.travis.shared.common.pipeline.BusinessExecutor;
import org.travis.shared.common.pipeline.ProcessContext;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName VmDeleteSnapshotAction
 * @Description 删除快照文件 + 数据库
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class VmDeleteSnapshotAction implements BusinessExecutor<VmwareDestroyPipe> {
    @Resource
    private SnapshotInfoMapper snapshotInfoMapper;
    @Resource
    private AgentAssistService agentAssistService;
    @DubboReference
    private AgentHostClient agentHostClient;

    @Override
    public void execute(ProcessContext<VmwareDestroyPipe> context) {
        VmwareDestroyPipe dataModel = context.getDataModel();
        VmwareInfo vmwareInfo = dataModel.getVmwareInfo();
        HostInfo hostInfo = dataModel.getHostInfo();

        try {
            List<SnapshotInfo> snapshotInfos = snapshotInfoMapper.selectList(
                    Wrappers.<SnapshotInfo>lambdaQuery().eq(SnapshotInfo::getVmwareId, vmwareInfo.getId())
            );

            if (snapshotInfos == null || snapshotInfos.isEmpty()) {
                log.info("虚拟机快照文件不存在，无需清理！");
                return;
            }

            String hostSharedStoragePath = agentAssistService.getHostSharedStoragePath();

            // 删除快照文件
            List<String> collect = snapshotInfos.stream().map(tmp -> hostSharedStoragePath + tmp.getSubPath()).collect(Collectors.toList());
            R<Void> deleted = agentHostClient.deleteFiles(hostInfo.getIp(), collect);
            if (deleted.checkFail()) {
                log.warn("虚拟机快照清理失败：{}", deleted.getMsg());
            } else {
                log.info("虚拟机快照清理成功！");
            }

            // 删除数据库记录
            snapshotInfoMapper.deleteBatchIds(snapshotInfos.stream().map(SnapshotInfo::getId).collect(Collectors.toList()));

        } catch (PipelineProcessException pipelineProcessException) {
            log.error("虚拟机快照清理失败：{}", pipelineProcessException.getMessage());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机快照清理失败：" + pipelineProcessException.getMessage()));
            context.setNeedBreak(true);
        } catch (Exception e) {
            log.error("「未知异常」虚拟机快照清理失败：{}", e.toString());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "「未知异常」虚拟机快照清理失败：" + e.getMessage()));
            context.setNeedBreak(true);
        }
    }
}
