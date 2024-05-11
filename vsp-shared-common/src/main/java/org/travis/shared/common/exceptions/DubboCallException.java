package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName DubboCallException
 * @Description Dubbo 远程调用异常
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
@Getter
public class DubboCallException extends CommonException{
    private static final int CODE = BizCodeEnum.DUBBO_CALL_ERROR.getCode();
    private static final String MESSAGE = BizCodeEnum.DUBBO_CALL_ERROR.getMessage();

    public DubboCallException() {
        this(MESSAGE);
    }

    public DubboCallException(String message) {
        this(CODE, message);
    }

    public DubboCallException(int code, String message) {
        super(code, message);
    }

}
