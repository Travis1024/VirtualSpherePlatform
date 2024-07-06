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
    AUTH_FAILED(401, "未通过身份验证"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源或请求未找到"),
    LOCKED(423, "请求失败, 请稍后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁, 请稍后重试"),
    /**
     * 服务端错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    /**
     * 自定义服务错误
     */
    DUBBO_CALL_ERROR(940, "Dubbo-服务提供者调用异常"),
    DUBBO_FUNCTION_ERROR(941, "Dubbo-远程方法执行异常"),
    DUBBO_HEALTHY_CHECK_ERROR(942, "Dubbo-健康检查失败"),

    INTERFACE_ANTI_SHAKE_ERROR(950, "接口防抖异常"),

    HOST_RESOURCE_LACK(960, "宿主机资源不足"),

    PIPELINE_ERROR(990, "责任链纸执行异常"),
    PIPELINE_CONTEXT_IS_NULL(991, "责任链上下文为空"),
    PIPELINE_BUSINESS_CODE_IS_NULL(992, "责任链业务状态码为空"),
    PIPELINE_PROCESS_TEMPLATE_IS_NULL(993, "责任链流程模板为空"),
    PIPELINE_PROCESS_TEMPLATE_EXECUTOR_ACTION_LIST_IS_NULL(994, "责任链流程模板动作列表为空"),

    UNKNOW(999, "未知错误"),

    INTERNAL_MESSAGE(1000, "内部处理消息"),
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
