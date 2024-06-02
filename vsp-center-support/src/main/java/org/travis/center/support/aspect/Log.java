package org.travis.center.support.aspect;

import org.travis.center.common.enums.BusinessTypeEnum;

import java.lang.annotation.*;

/**
 * @ClassName Log
 * @Description Log
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能
     */
    BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;

    /**
     * 排除指定的请求参数
     */
    String[] excludeParamNames() default {};
}
