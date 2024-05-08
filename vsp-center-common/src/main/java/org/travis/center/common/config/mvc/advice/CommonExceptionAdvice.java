package org.travis.center.common.config.mvc.advice;

import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.NestedServletException;
import org.travis.center.common.enums.BizCodeEnum;
import org.travis.center.common.exceptions.CommonException;
import org.travis.center.common.pojo.domain.R;
import org.travis.center.common.utils.RequestInfoUtil;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @ClassName CommonExceptionAdvice
 * @Description 统一异常处理类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionAdvice {

    @ExceptionHandler(CommonException.class)
    public Object handleDatabaseException(CommonException exception) {
        log.error("[自定义异常] -> 异常类:{}, 状态码:{}, 异常信息:", exception.getClass().getName(), exception.getCode(), exception);
        return processResponse(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public Object handleNotLoginException(NotLoginException exception) {
        log.error("[登录鉴权异常] -> 异常类:{}, 状态码:{}, 异常信息:", exception.getClass().getName(), BizCodeEnum.MISSING_TOKEN, exception);
        return processResponse(BizCodeEnum.MISSING_TOKEN.getCode(), BizCodeEnum.MISSING_TOKEN.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        // 拼接请求参数校验异常信息
        String message = exception.getBindingResult().getAllErrors()
                .stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("|"));
        log.error("[请求参数校验异常] -> 异常类:{}, 状态码:{}, 异常信息:", exception.getClass().getName(), 400, exception);
        return processResponse(BizCodeEnum.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException exception) {
        log.error("[请求参数绑定异常] -> 异常类:{}, 状态码:{}, 异常信息:", exception.getClass().getName(), 400, exception);
        return processResponse(BizCodeEnum.BAD_REQUEST.getCode(), exception.getMessage());
    }

    @ExceptionHandler(NestedServletException.class)
    public Object handleNestedServletException(NestedServletException exception) {
        log.error("[嵌套服务异常] -> 异常类:{}, 状态码:{}, 异常信息:", exception.getClass().getName(), 400, exception);
        return processResponse(BizCodeEnum.BAD_REQUEST.getCode(), exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations()
                .stream().map(ConstraintViolation::getMessage)
                .distinct().collect(Collectors.joining("|"));
        log.error("[约束违反异常] -> 异常类:{}, 状态码:{}, 异常信息:", exception.getClass().getName(), 400, exception);
        return processResponse(BizCodeEnum.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Object handleRuntimeException(Exception exception) {
        log.error("[未知异常] -> 异常类:{}, 状态码:{}, URI:{}, 异常信息:",
                exception.getClass().getName(),
                BizCodeEnum.UNKNOW.getCode(),
                RequestInfoUtil.getRequest() != null ? RequestInfoUtil.getRequest().getRequestURI() : "NULL",
                exception
        );
        return processResponse(BizCodeEnum.INTERNAL_SERVER_ERROR.getCode(), BizCodeEnum.INTERNAL_SERVER_ERROR.getMessage());
    }

    /**
     * @MethodName processResponse
     * @Description 包装错误响应请求，前端基于业务状态码code来判断状态。
     * @Author travis-wei
     * @Data 2024/5/8
     * @param code	状态码
     * @param msg	消息
     * @Return java.lang.Object
     **/
    private Object processResponse(int code, String msg){
        return R.error(code, msg);
    }
}
