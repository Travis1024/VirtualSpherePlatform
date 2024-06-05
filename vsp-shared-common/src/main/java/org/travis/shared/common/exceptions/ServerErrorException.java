package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName ServerErrorException
 * @Description 服务器异常, 500
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/5
 */
@Getter
public class ServerErrorException extends CommonException {
    private static final int CODE = BizCodeEnum.INTERNAL_SERVER_ERROR.getCode();
    private static final String MESSAGE = BizCodeEnum.INTERNAL_SERVER_ERROR.getMessage();

    public ServerErrorException() {
        this(MESSAGE);
    }

    public ServerErrorException(String message) {
        this(CODE, message);
    }

    public ServerErrorException(int code, String message) {
        super(code, message);
    }
}
