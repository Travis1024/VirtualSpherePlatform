package org.travis.shared.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName CorsConfig
 * @Description 跨域配置
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
@Configuration
public class CorsConfig {
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许任何域名
        corsConfiguration.addAllowedOriginPattern("*");
        // 允许任何头
        corsConfiguration.addAllowedHeader("*");
        // 允许任何方法
        corsConfiguration.addAllowedMethod("*");
        // 允许暴露任何头部信息
        corsConfiguration.addExposedHeader("*");
        // 设置预检请求缓存时间
        corsConfiguration.setMaxAge(3600L);
        // 允许 cookie 跨域
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(new CorsFilter(source));
        // 设置 Filter 优先级
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}


