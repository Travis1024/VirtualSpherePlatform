package org.travis.center.common.config.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.cluster.router.state.StateRouter;
import org.apache.dubbo.rpc.cluster.router.state.StateRouterFactory;

/**
 * @ClassName SelectByIpRouterFactory
 * @Description SelectByIpRouterFactory
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
public class SelectByIpRouterFactory implements StateRouterFactory {
    @Override
    public <T> StateRouter<T> getRouter(Class<T> interfaceClass, URL url) {
        return new SelectByIpRouter<>(url);
    }
}
