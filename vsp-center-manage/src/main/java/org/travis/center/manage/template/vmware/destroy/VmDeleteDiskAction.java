package org.travis.center.manage.template.vmware.destroy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.travis.api.client.agent.AgentHostClient;
import org.travis.center.common.entity.manage.DiskInfo;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.mapper.manage.DiskInfoMapper;
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
 * @ClassName VmDeleteDiskAction
 * @Description 删除磁盘文件 + 数据库
 * @Author Travis
 * @Data 2024/09
 */
@Slf4j
@Service
public class VmDeleteDiskAction implements BusinessExecutor<VmwareDestroyPipe> {
    @Resource
    private DiskInfoMapper diskInfoMapper;
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
            List<DiskInfo> diskInfos = diskInfoMapper.selectList(
                    Wrappers.<DiskInfo>lambdaQuery().eq(DiskInfo::getVmwareId, vmwareInfo.getId())
            );

            if (diskInfos == null || diskInfos.isEmpty()) {
                log.info("虚拟机磁盘文件不存在，无需清理！");
                return;
            }

            String hostSharedStoragePath = agentAssistService.getHostSharedStoragePath();

            // 删除磁盘文件
            List<String> collect = diskInfos.stream().map(tmp -> hostSharedStoragePath + tmp.getSubPath()).collect(Collectors.toList());
            R<Void> deleted = agentHostClient.deleteFiles(hostInfo.getIp(), collect);
            if (deleted.checkFail()) {
                log.warn("虚拟机磁盘清理失败：{}", deleted.getMsg());
            } else {
                log.info("虚拟机磁盘清理成功！");
            }

            // 删除数据库记录
            diskInfoMapper.deleteBatchIds(diskInfos.stream().map(DiskInfo::getId).collect(Collectors.toList()));

        } catch (PipelineProcessException pipelineProcessException) {
            log.error("虚拟机磁盘清理失败：{}", pipelineProcessException.getMessage());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "虚拟机磁盘清理失败：" + pipelineProcessException.getMessage()));
            context.setNeedBreak(true);
        } catch (Exception e) {
            log.error("「未知异常」虚拟机磁盘清理失败：{}", e.toString());
            context.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), "「未知异常」虚拟机磁盘清理失败：" + e.getMessage()));
            context.setNeedBreak(true);
        }
    }
}
