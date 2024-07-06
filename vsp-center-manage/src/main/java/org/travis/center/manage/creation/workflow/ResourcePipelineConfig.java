package org.travis.center.manage.creation.workflow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.travis.center.common.enums.PipelineBusinessCodeEnum;
import org.travis.shared.common.pipeline.FlowController;
import org.travis.shared.common.pipeline.ProcessTemplate;

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

    @Bean("snapshotCreateTemplate")
    public ProcessTemplate snapshotCreateTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessTemplateActionList(
                // TODO 新增 snapshotCreateTemplate 动作
                Arrays.asList()
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
