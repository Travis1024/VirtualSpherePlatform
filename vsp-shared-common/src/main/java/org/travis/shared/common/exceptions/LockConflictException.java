package org.travis.shared.common.exceptions;

import lombok.Getter;
import org.travis.shared.common.enums.BizCodeEnum;

/**
 * @ClassName LockConflictException
 * @Description 锁冲突异常, 423
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Getter
public class LockConflictException extends CommonException {

    private static final int CODE = BizCodeEnum.LOCKED.getCode();
    private static final String MESSAGE = BizCodeEnum.LOCKED.getMessage();

    public LockConflictException() {
        this(MESSAGE);
    }

    public LockConflictException(String message) {
        this(CODE, message);
    }

    public LockConflictException(int code, String message) {
        super(code, message);
    }
}
