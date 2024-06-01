package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName NotFoundException
 * @Description 请求参数异常类, 404
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Getter
public class NotFoundException extends CommonException {

    private static final int CODE = BizCodeEnum.NOT_FOUND.getCode();
    private static final String MESSAGE = BizCodeEnum.NOT_FOUND.getMessage();

    public NotFoundException() {
        this(MESSAGE);
    }

    public NotFoundException(String message) {
        this(CODE, message);
    }

    public NotFoundException(int code, String message) {
        super(code, message);
    }
}
