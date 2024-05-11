package org.travis.shared.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.travis.shared.common.filter.RequestIdFilter;

import javax.servlet.Filter;

/**
 * @ClassName MvcAutoConfig
 * @Description MVC 相关类注入
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Configuration
@ConditionalOnClass({Filter.class})
public class MvcAutoConfig implements WebMvcConfigurer {

    /**
     * 将「请求 ID 过滤器」注入 Bean 容器
     */
    @Bean
    @ConditionalOnMissingBean
    public RequestIdFilter requestIdFilter() {
        return new RequestIdFilter();
    }

    /**
     * 将「响应信息包装类」注入 Bean 容器
     */
    @Bean
    @ConditionalOnMissingBean
    public WrapperResponseBodyAdvice wrapperResponseBodyAdvice() {
        return new WrapperResponseBodyAdvice();
    }
}
