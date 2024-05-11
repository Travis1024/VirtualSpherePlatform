package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName DubboFunctionException
 * @Description Dubbo 远程方法执行异常
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
@Getter
public class DubboFunctionException extends CommonException{
    private static final int CODE = BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode();
    private static final String MESSAGE = BizCodeEnum.DUBBO_FUNCTION_ERROR.getMessage();

    public DubboFunctionException() {
        this(MESSAGE);
    }

    public DubboFunctionException(String message) {
        this(CODE, message);
    }

    public DubboFunctionException(int code, String message) {
        super(code, message);
    }

}
