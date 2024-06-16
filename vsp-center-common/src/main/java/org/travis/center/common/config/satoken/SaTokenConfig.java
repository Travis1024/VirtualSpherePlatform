package org.travis.center.common.config.satoken;

import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.travis.center.common.interceptor.MySaInterceptor;
import org.travis.center.common.interceptor.UserInfoInterceptor;

import javax.annotation.Resource;

/**
 * @ClassName SaTokenConfig
 * @Description SaTokenConfig
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Resource
    public UserInfoInterceptor userInfoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录鉴权拦截器
        registry.addInterceptor(new MySaInterceptor(handle -> {
                    SaRouter
                            // 匹配需要鉴权的所有路径
                            .match("/**")
                            // 排除 Options 请求
                            .notMatch(SaHttpMethod.OPTIONS)
                            // 鉴权逻辑
                            .check(r -> StpUtil.checkLogin());
                }))
                .addPathPatterns("/**")
                // swagger
                .excludePathPatterns("/favicon.ico")
                .excludePathPatterns("/v2/**")
                .excludePathPatterns("/v3/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/doc.html")
                .excludePathPatterns("/error")
                // 鉴权
                .excludePathPatterns("/user/login");

        // 添加用户信息 ThreadLocal 拦截器
        registry.addInterceptor(userInfoInterceptor);
    }
}
