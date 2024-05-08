package org.travis.center.common.interceptor;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName MySaInterceptor
 * @Description MySaInterceptor
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
@Slf4j
public class MySaInterceptor extends SaInterceptor {

    public MySaInterceptor(SaParamFunction<Object> auth) {
        super.auth = auth;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[URL-鉴权] -> {}", request.getRequestURI());
        return super.preHandle(request, response, handler);
    }
}
