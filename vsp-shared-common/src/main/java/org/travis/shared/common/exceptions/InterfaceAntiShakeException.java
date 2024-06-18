package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName InterfaceAntiShakeException
 * @Description 接口防抖异常类, 950
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/19
 */
@Getter
public class InterfaceAntiShakeException extends CommonException {

    private static final int CODE = BizCodeEnum.INTERFACE_ANTI_SHAKE_ERROR.getCode();
    private static final String MESSAGE = BizCodeEnum.INTERFACE_ANTI_SHAKE_ERROR.getMessage();

    public InterfaceAntiShakeException() {
        this(MESSAGE);
    }

    public InterfaceAntiShakeException(String message) {
        this(CODE, message);
    }

    public InterfaceAntiShakeException(int code, String message) {
        super(code, message);
    }
}
