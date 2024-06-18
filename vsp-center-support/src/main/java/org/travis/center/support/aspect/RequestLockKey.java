package org.travis.center.support.aspect;

import java.lang.annotation.*;

/**
 * @ClassName RequestLockKey
 * @Description 加上这个注解可以将参数设置为key
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/18
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestLockKey {
}
