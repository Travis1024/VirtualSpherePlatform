package org.travis.shared.common.exceptions;


import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName AuthFailedException
 * @Description 身份验证失败, 401
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
public class AuthFailedException extends CommonException {

    private static final int CODE = BizCodeEnum.AUTH_FAILED.getCode();
    private static final String MESSAGE = BizCodeEnum.AUTH_FAILED.getMessage();


    public AuthFailedException() {
        this(MESSAGE);
    }

    public AuthFailedException(String message) {
        this(CODE, message);
    }

    public AuthFailedException(int code, String message) {
        super(code, message);
    }
}
