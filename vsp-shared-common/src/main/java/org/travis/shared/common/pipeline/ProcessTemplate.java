package org.travis.shared.common.pipeline;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ProcessTemplate
 * @Description 业务执行模板（把责任链的逻辑串起来）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
public class ProcessTemplate {

    /**
     * 业务执行模版（ProcessTemplate）会注入到 Bean 中
     * 业务模版在 XxxPipelineConfig 类中进行初始化
     *
     * 示例：send 模版（Bean名字 -> commonSendTemplate）
     *     1. 前置参数校验    SendPreCheckAction（BusinessProcess）
     *     2. 组装参数       SendAssembleAction（BusinessProcess）
     *     3. 后置参数校验    SendAfterCheckAction（BusinessProcess）
     *     4. 发送消息至MQ   SendMqAction（BusinessProcess）
     */

    private List<BusinessExecutor> processTemplateActionList;
}
