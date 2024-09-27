package org.travis.shared.common.pipeline;

import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.PipelineProcessException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ProcessController
 * @Description 流程控制器 (存在两个 bean：apiProcessController 和 handlerProcessController)
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
@Component
public class FlowController {

    @Resource
    public ApplicationContext applicationContext;

    /**
     * 流程模版存储, 模版映射：<BusinessCode, ProcessTemplate>
     */
    private Map<String, ProcessTemplate> processTemplateMap = null;

    /**
     * 执行责任链（责任链上下文 = 责任链业务代码 + 上下文数据模型 + 中断标识 + 流程处理结果）
     *
     * @params {@link ProcessContext}
     * @return 返回上下文内容
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ProcessContext<?> process(ProcessContext<?> processContext) {
        // 1.前置检查（责任链上下文、业务代码、执行模版、执行动作列表）
        try {
            preCheck(processContext);
        } catch (PipelineProcessException e) {
            return e.getProcessContext();
        }

        // 2.获取「业务类型」所对应的「流程模版」的「业务动作列表」
        List<BusinessExecutor> processTemplateActionList = processTemplateMap.get(processContext.getBusinessCode()).getProcessTemplateActionList();

        // 3.遍历流程模版中的每一步业务动作，并执行；每一步执行完成之后判断是否因为异常需要中断执行。
        for (BusinessExecutor businessProcess : processTemplateActionList) {
            try {
                businessProcess.execute(processContext);
            } catch (Exception e) {
                processContext.setNeedBreak(true);
                processContext.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), e.getMessage()));
            }
            if (processContext.getNeedBreak()) {
                break;
            }
        }
        return processContext;
    }

    /**
     * 「事务」执行责任链（责任链上下文 = 责任链业务代码 + 上下文数据模型 + 中断标识 + 流程处理结果）
     *
     * @params {@link ProcessContext}
     * @return 返回上下文内容
     */
    @Transactional(rollbackFor = {PipelineProcessException.class, CommonException.class, Exception.class})
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ProcessContext<?> processInTransaction(ProcessContext<?> processContext) {
        // 1.前置检查（责任链上下文、业务代码、执行模版、执行动作列表）
        try {
            preCheck(processContext);
        } catch (PipelineProcessException e) {
            return e.getProcessContext();
        }

        // 2.获取「业务类型」所对应的「流程模版」的「业务动作列表」
        List<BusinessExecutor> processTemplateActionList = processTemplateMap.get(processContext.getBusinessCode()).getProcessTemplateActionList();

        // 3.遍历流程模版中的每一步业务动作，并执行；每一步执行完成之后判断是否因为异常需要中断执行。
        for (BusinessExecutor businessProcess : processTemplateActionList) {
            try {
                BusinessExecutor proxy = applicationContext.getBean(businessProcess.getClass());
                proxy.execute(processContext);
            } catch (Exception e) {
                processContext.setNeedBreak(true);
                processContext.setResponse(R.error(BizCodeEnum.PIPELINE_ERROR.getCode(), e.getMessage()));
            }
            if (processContext.getNeedBreak()) {
                throw new PipelineProcessException(processContext);
            }
        }
        return processContext;
    }

    /**
     * 执行前检查，出错则抛出异常
     *
     * @param processContext 执行上下文
     * @throws PipelineProcessException 异常信息
     */
    @SuppressWarnings("rawtypes")
    private void preCheck(ProcessContext<?> processContext) throws PipelineProcessException {
        // 1.检查责任链上下文 (如果责任链上下文为空，则返回为空的异常)
        if (Objects.isNull(processContext)) {
            processContext = ProcessContext.builder().response(R.error(BizCodeEnum.PIPELINE_CONTEXT_IS_NULL.getCode(), BizCodeEnum.PIPELINE_CONTEXT_IS_NULL.getMessage())).build();
            throw new PipelineProcessException(processContext);
        }

        // 2.检查业务代码 (如果业务代码为空，则返回为空的异常)
        String businessCode = processContext.getBusinessCode();
        if (Objects.isNull(businessCode)) {
            processContext.setResponse(R.error(BizCodeEnum.PIPELINE_BUSINESS_CODE_IS_NULL.getCode(), BizCodeEnum.PIPELINE_BUSINESS_CODE_IS_NULL.getMessage()));
            throw new PipelineProcessException(processContext);
        }

        // 3.检查执行模板 (如果执行模版为空，则返回为空的异常)
        ProcessTemplate processTemplate = processTemplateMap.get(businessCode);
        if (Objects.isNull(processTemplate)) {
            processContext.setResponse(R.error(BizCodeEnum.PIPELINE_PROCESS_TEMPLATE_IS_NULL.getCode(), BizCodeEnum.PIPELINE_PROCESS_TEMPLATE_IS_NULL.getMessage()));
            throw new PipelineProcessException(processContext);
        }

        // 4.检查执行模板 -> 执行动作列表 (如果执行动作列表为空，则返回为空的异常)
        List<BusinessExecutor> processList = processTemplate.getProcessTemplateActionList();
        if (Objects.isNull(processList) || processList.isEmpty()) {
            processContext.setResponse(R.error(BizCodeEnum.PIPELINE_PROCESS_TEMPLATE_EXECUTOR_ACTION_LIST_IS_NULL.getCode(), BizCodeEnum.PIPELINE_PROCESS_TEMPLATE_EXECUTOR_ACTION_LIST_IS_NULL.getMessage()));
            throw new PipelineProcessException(processContext);
        }
    }
}
