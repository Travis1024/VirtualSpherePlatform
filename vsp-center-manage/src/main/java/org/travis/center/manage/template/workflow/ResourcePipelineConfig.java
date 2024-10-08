package org.travis.center.manage.template.workflow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.travis.center.common.enums.PipelineBusinessCodeEnum;
import org.travis.center.manage.template.snapshot.build.SnapshotCreatePreCheckAction;
import org.travis.center.manage.template.snapshot.build.SnapshotHistoryMergeAction;
import org.travis.center.manage.template.snapshot.build.SnapshotLatestCreateAction;
import org.travis.center.manage.template.snapshot.resume.SnapshotDiskCleanAction;
import org.travis.center.manage.template.snapshot.resume.SnapshotDiskMountAction;
import org.travis.center.manage.template.snapshot.resume.SnapshotDiskUnmountAction;
import org.travis.center.manage.template.vmware.destroy.*;
import org.travis.shared.common.pipeline.FlowController;
import org.travis.shared.common.pipeline.ProcessTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ApiPipelineConfig
 * @Description Pipeline 配置类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Configuration
public class ResourcePipelineConfig {
    @Resource
    private SnapshotCreatePreCheckAction snapshotCreatePreCheckAction;
    @Resource
    private SnapshotHistoryMergeAction snapshotHistoryMergeAction;
    @Resource
    private SnapshotLatestCreateAction snapshotLatestCreateAction;
    @Resource
    private SnapshotDiskCleanAction snapshotDiskCleanAction;
    @Resource
    private SnapshotDiskMountAction snapshotDiskMountAction;
    @Resource
    private SnapshotDiskUnmountAction snapshotDiskUnmountAction;
    @Resource
    private VmCancelDefineAction vmCancelDefineAction;
    @Resource
    private VmConfigCleanAction vmConfigCleanAction;
    @Resource
    private VmDeleteDiskAction vmDeleteDiskAction;
    @Resource
    private VmDeleteRecordAction vmDeleteRecordAction;
    @Resource
    private VmDeleteSnapshotAction vmDeleteSnapshotAction;
    @Resource
    private VmStatusCheckAction vmStatusCheckAction;

    /**
     * 虚拟机快照创建过程
     * 1. 创建前检查
     * 2. 历史快照合并
     * 3. 最新快照创建
     */
    public ProcessTemplate snapshotCreateTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessTemplateActionList(
                Arrays.asList(
                        // 创建前检查
                        snapshotCreatePreCheckAction,
                        // 历史快照合并
                        snapshotHistoryMergeAction,
                        // 最新快照创建
                        snapshotLatestCreateAction
                )
        );
        return processTemplate;
    }

    /**
     * 虚拟机快照恢复流程
     * 1.虚拟机关闭 + 移除虚拟机硬盘
     * 2.挂载原有虚拟机磁盘 + 重启虚拟机
     * 3.删除原有快照信息 + 删除快照文件 + 删除数据库快照记录
     */
    public ProcessTemplate snapshotResumeTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessTemplateActionList(
                Arrays.asList(
                        // 虚拟机关闭 + 移除虚拟机硬盘
                        snapshotDiskUnmountAction,
                        // 挂载原有虚拟机磁盘 + 重启虚拟机
                        snapshotDiskMountAction,
                        // 删除原有快照信息 + 删除快照文件 + 删除数据库快照记录
                        snapshotDiskCleanAction
                )
        );
        return processTemplate;
    }

    /**
     * 虚拟机删除流程
     * 1.判断虚拟机状态（强制关闭）
     * 2.删除监测任务 + 删除动态配置记录
     * 3.取消虚拟机定义
     * 4.删除快照文件 + 数据库
     * 5.删除磁盘文件 + 数据库
     * 6.删除虚拟机数据库记录（vm + vmXml + vm权限关联）
     */
    public ProcessTemplate vmwareDestroyTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessTemplateActionList(
                Arrays.asList(
                        // 判断虚拟机状态（强制关闭）
                        vmStatusCheckAction,
                        // 删除监测任务 + 删除动态配置记录
                        vmConfigCleanAction,
                        // 取消虚拟机定义
                        vmCancelDefineAction,
                        // 删除快照文件 + 数据库
                        vmDeleteSnapshotAction,
                        // 删除磁盘文件 + 数据库
                        vmDeleteDiskAction,
                        // 删除虚拟机数据库记录（vm + vmXml + vm权限关联）
                        vmDeleteRecordAction
                )
        );
        return processTemplate;
    }


    @Bean("resourceFlowController")
    public FlowController resourceFlowController() {
        FlowController flowController = new FlowController();
        Map<String, ProcessTemplate> processTemplateMap = new HashMap<>();
        // 「START」流程构建
        processTemplateMap.put(PipelineBusinessCodeEnum.SNAPSHOT_CREATE.getCode(), snapshotCreateTemplate());
        processTemplateMap.put(PipelineBusinessCodeEnum.SNAPSHOT_RESUME.getCode(), snapshotResumeTemplate());
        processTemplateMap.put(PipelineBusinessCodeEnum.VMWARE_DESTROY.getCode(), vmwareDestroyTemplate());
        // 「 END 」流程构建
        flowController.setProcessTemplateMap(processTemplateMap);
        return flowController;
    }
}
