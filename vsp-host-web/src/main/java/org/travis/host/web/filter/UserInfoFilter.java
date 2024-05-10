package org.travis.host.web.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.travis.host.web.constants.SystemConstant;
import org.travis.host.web.utils.UserThreadLocalUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @ClassName UserInfoFilter
 * @Description UserInfoFilter
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/10
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "UserInfoFilter", urlPatterns = "/**")
public class UserInfoFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 1.获取请求头上的 userId
        String userId = request.getHeader(SystemConstant.USER_ID_HEADER);

        if (StrUtil.isEmpty(userId)) {
            log.warn("[请求警告] -> 未检测到用户ID!");
        } else {
            // 2.将 requestId 存入 MDC
            UserThreadLocalUtil.setUserId(Long.parseLong(userId));
        }

        // 3.执行业务方法
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
