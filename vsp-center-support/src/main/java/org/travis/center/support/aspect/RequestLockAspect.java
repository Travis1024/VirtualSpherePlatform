package org.travis.center.support.aspect;

import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.travis.shared.common.exceptions.InterfaceAntiShakeException;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @ClassName RequestLockAspect
 * @Description RequestLockAspect
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/18
 */
@Aspect
@Configuration
@Order(0)
public class RequestLockAspect {

    @Resource
    private RedissonClient redissonClient;

    @Around("execution(public * * (..)) && @annotation(org.travis.center.support.aspect.RequestLock)")
    public Object interceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestLock requestLock = method.getAnnotation(RequestLock.class);
        // if (StrUtil.isEmpty(requestLock.prefix())) {
        //     throw new InterfaceAntiShakeException("[接口防抖] 前缀不能为空!");
        // }
        // 获取自定义 key
        final String lockKey = RequestLockKeyGenerator.getLockKey(joinPoint);
        // 使用 Redisson 分布式锁的方式判断是否重复提交
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        try {
            // 尝试抢占锁
            isLocked = lock.tryLock();
            // 没有拿到锁说明已经有了请求了
            if (!isLocked) {
                throw new InterfaceAntiShakeException("[接口防抖] 您的操作太快了,请稍后重试~");
            }
            // 拿到锁后设置过期时间
            lock.lock(requestLock.expire(), requestLock.timeUnit());
            return joinPoint.proceed();
        } finally {
            // 释放锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
