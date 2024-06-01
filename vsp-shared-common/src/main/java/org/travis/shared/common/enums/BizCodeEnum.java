package org.travis.shared.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BizCodeEnum
 * @Description 业务状态枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
@Getter
public enum BizCodeEnum {
    /**
     * 处理成功
     */
    SUCCESS(200, "OK"),
    /**
     * 客户端错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    MISSING_TOKEN(401, "未能读取到有效-Token"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源或请求未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不正确"),
    LOCKED(423, "请求失败, 请稍后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁, 请稍后重试"),
    /**
     * 服务端错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    /**
     * 自定义服务错误
     */
    TOKEN_CHECK_FAILED(901, "Token 验证失败"),
    DATABASE_OPERATION_FAILED(920, "数据库操作异常"),
    DUBBO_CALL_ERROR(940, "Dubbo-服务提供者调用异常"),
    DUBBO_FUNCTION_ERROR(941, "Dubbo-远程方法执行异常"),
    DUBBO_HEALTHY_CHECK_ERROR(942, "Dubbo-健康检查失败"),
    /**
     * 未知错误
     */
    UNKNOW(999, "未知错误")
    ;

    /**
     * 启动时初始化异常类 Map
     */
    public static final Map<Integer, BizCodeEnum> BIZ_CODE_ENUM_MAP = new HashMap<>(BizCodeEnum.values().length);

    static {
        for (BizCodeEnum anEnum : BizCodeEnum.values()) {
            BIZ_CODE_ENUM_MAP.put(anEnum.getCode(), anEnum);
        }
    }

    /**
     * 业务状态码
     */
    private final int code;
    /**
     * 业务状态消息
     */
    private final String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
