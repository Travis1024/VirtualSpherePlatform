package org.travis.center.support.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.travis.center.common.entity.support.OperationLog;
import org.travis.center.common.enums.BusinessStateEnum;
import org.travis.center.common.mapper.support.OperationLogMapper;
import org.travis.center.support.filter.PropertyPreExcludeFilter;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.utils.NetworkUtil;
import org.travis.shared.common.utils.ServletUtil;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.UserThreadLocalUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * @ClassName LogAspect
 * @Description LogAspect
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private OperationLogMapper operationLogMapper;
    @Resource
    private RedissonClient redissonClient;

    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");


    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void boBefore(JoinPoint joinPoint, Log controllerLog) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }


    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            // 获取当前的用户
            Long userId = UserThreadLocalUtil.getUserId();

            // 初始化日志记录
            OperationLog operationLog = new OperationLog();
            operationLog.setId(SnowflakeIdUtil.nextId());
            operationLog.setOperationState(BusinessStateEnum.SUCCESS);
            // 设置请求的IP地址
            String ip = NetworkUtil.getIpAddr();
            operationLog.setIpAddress(ip);
            operationLog.setRequestUrl(StrUtil.sub(ServletUtil.getRequest().getRequestURI(), 0, 255));
            // 设置操作用户
            if (userId != null) {
                operationLog.setUserId(userId);
                operationLog.setCreator(userId);
                operationLog.setUpdater(userId);
            }
            // 设置操作异常信息
            if (e != null) {
                operationLog.setOperationState(BusinessStateEnum.FAIL);
                operationLog.setErrorMessage(e.getMessage());
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operationLog.setMethod(className + "." + methodName + "()");
            operationLog.setRequestMethod(ServletUtil.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operationLog, jsonResult);
            // 设置消耗时间
            operationLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 保存到 redis 缓存
            RBlockingDeque<OperationLog> blockingDeque = redissonClient.getBlockingDeque(RedissonConstant.LOG_CACHE_DATA_KEY);
            blockingDeque.offer(operationLog);

        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param operationLog 操作日志
     * @throws Exception 异常
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperationLog operationLog, Object jsonResult) throws Exception {
        // 设置action动作
        operationLog.setBusinessType(log.businessType());
        // 设置标题
        operationLog.setTitle(log.title());
        // 是否需要保存 request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operationLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && ObjectUtil.isNotNull(jsonResult)) {
            operationLog.setResponseInfo(JSONUtil.toJsonStr(jsonResult));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operationLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, OperationLog operationLog, String[] excludeParamNames) throws Exception {
        String requestMethod = operationLog.getRequestMethod();
        Map<?, ?> paramsMap = ServletUtil.getParamMap(ServletUtil.getRequest());
        if (CollectionUtil.isEmpty(paramsMap)
                && (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod))) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operationLog.setRequestParams(params);
        } else {
            operationLog.setRequestParams(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object o : paramsArray) {
                if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames));
                        params.append(jsonObj).append(StrUtil.SPACE);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames) {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames));
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
