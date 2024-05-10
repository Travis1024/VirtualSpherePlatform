package org.travis.host.web.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.travis.host.web.constants.SystemConstant;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @ClassName RequestIdFilter
 * @Description 请求 ID 过滤器
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/4/21
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "RequestIdFilter", urlPatterns = "/**")
public class RequestIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            // 1.获取请求头上的 requestId
            String requestId = request.getHeader(SystemConstant.REQUEST_ID_HEADER);
            if (StrUtil.isEmpty(requestId)) {
                log.error("[请求拦截] -> 未检测到请求ID!");
                return;
            }
            // 2.将 requestId 存入 MDC
            MDC.put(SystemConstant.REQUEST_ID_HEADER, requestId);
            // 3.执行业务方法
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 业务执行结束后：移除 MDC
            MDC.clear();
        }
    }
}
