package org.travis.center.common.config.mvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.travis.center.common.interceptor.UserInfoInterceptor;

import javax.servlet.Filter;

/**
 * @ClassName MvcAutoConfig
 * @Description MVC 相关类注入
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Configuration
@ConditionalOnClass({CommonExceptionAdvice.class, Filter.class})
public class MvcAutoConfig implements WebMvcConfigurer {
    /**
     * 将「统一异常处理类」注入 Bean 容器
     */
    @Bean
    @ConditionalOnMissingBean
    public CommonExceptionAdvice commonExceptionAdvice() {
        return new CommonExceptionAdvice();
    }

    /**
     * 将「用户ID拦截器」注入 Bean 容器
     */
    @Bean
    @ConditionalOnMissingBean
    public UserInfoInterceptor userInfoInterceptor() {
        return new UserInfoInterceptor();
    }
}
