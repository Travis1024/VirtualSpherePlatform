package org.travis.center.common.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.travis.center.common.utils.UserThreadLocalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserInfoInterceptor
 * @Description UserInfoInterceptor
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断用户是否登录，如果登录，将 UserId 放入 ThreadLocal
        if (StpUtil.isLogin()) {
            UserThreadLocalUtil.setUserId(StpUtil.getLoginIdAsLong());
        }
        return true;
    }
}
