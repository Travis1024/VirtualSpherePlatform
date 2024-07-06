package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.pipeline.ProcessContext;

import java.util.Objects;

/**
 * @ClassName PipelineProcessException
 * @Description 责任链异常处理类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Getter
public class PipelineProcessException extends CommonException {

    private static final int CODE = BizCodeEnum.PIPELINE_ERROR.getCode();
    private static final String MESSAGE = BizCodeEnum.PIPELINE_ERROR.getMessage();

    /**
     *  获取责任链流程处理上下文
     */
    private ProcessContext<?> processContext;

    public PipelineProcessException() {
        this(MESSAGE);
    }

    public PipelineProcessException(String message) {
        this(CODE, message);
    }

    public PipelineProcessException(int code, String message) {
        super(code, message);
    }

    public PipelineProcessException(ProcessContext<?> processContext) {
        this(MESSAGE);
        this.processContext = processContext;
    }

    public PipelineProcessException(ProcessContext<?> processContext, String message) {
        this(message);
        this.processContext = processContext;
    }

    public PipelineProcessException(ProcessContext<?> processContext, Throwable cause) {
        super(CODE, cause.getMessage(), cause);
        this.processContext = processContext;
    }

    /**
     * 获取异常类-流程处理上下文-响应错误信息
     */
    @Override
    public String getMessage() {
        if (Objects.nonNull(this.processContext)) {
            return this.processContext.getResponse().getMsg();
        }
        return BizCodeEnum.PIPELINE_ERROR.getMessage();
    }
}
