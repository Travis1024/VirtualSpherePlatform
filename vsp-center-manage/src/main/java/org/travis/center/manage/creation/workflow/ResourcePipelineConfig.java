package org.travis.center.manage.creation.workflow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.travis.center.common.enums.PipelineBusinessCodeEnum;
import org.travis.center.manage.creation.snapshot.SnapshotCreatePreCheckAction;
import org.travis.center.manage.creation.snapshot.SnapshotHistoryMergeAction;
import org.travis.center.manage.creation.snapshot.SnapshotLatestCreateAction;
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

    @Bean("resourceFlowController")
    public FlowController resourceFlowController() {
        FlowController flowController = new FlowController();
        Map<String, ProcessTemplate> processTemplateMap = new HashMap<>();
        processTemplateMap.put(PipelineBusinessCodeEnum.SNAPSHOT_CREATE.getCode(), snapshotCreateTemplate());
        flowController.setProcessTemplateMap(processTemplateMap);
        return flowController;
    }
}
