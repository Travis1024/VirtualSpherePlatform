package org.travis.shared.common.pipeline;

/**
 * @ClassName BusinessExecutor<T extends ProcessModel>
 * @Description 业务执行器-接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
public interface BusinessExecutor<T extends ProcessModel> {

    /**
     * 真正处理逻辑
     *
     * @param context 责任链上下文信息
     */
    void execute(ProcessContext<T> context);
}
