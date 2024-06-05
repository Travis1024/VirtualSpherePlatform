package org.travis.shared.common.exceptions;


import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName ForbiddenException
 * @Description 权限异常类, 403
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
public class ForbiddenException extends CommonException {

    private static final int CODE = BizCodeEnum.FORBIDDEN.getCode();
    private static final String MESSAGE = BizCodeEnum.FORBIDDEN.getMessage();


    public ForbiddenException() {
        this(MESSAGE);
    }

    public ForbiddenException(String message) {
        this(CODE, message);
    }

    public ForbiddenException(int code, String message) {
        super(code, message);
    }
}
