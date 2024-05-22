package org.travis.center.common.config.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.Holder;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.RouterSnapshotNode;
import org.apache.dubbo.rpc.cluster.router.state.AbstractStateRouter;
import org.apache.dubbo.rpc.cluster.router.state.BitList;

/**
 * @ClassName SelectByIpRouter
 * @Description SelectByIpRouter
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
@Slf4j
public class SelectByIpRouter<T> extends AbstractStateRouter<T> {

    public SelectByIpRouter(URL url) {
        super(url);
    }

    @Override
    protected BitList<Invoker<T>> doRoute(BitList<Invoker<T>> invokers, URL url, Invocation invocation, boolean needToPrintMessage, Holder<RouterSnapshotNode<T>> routerSnapshotNodeHolder, Holder<String> messageHolder) throws RpcException {
        BitList<Invoker<T>> ipInvokers = invokers.clone();
        ipInvokers.clear();

        // 获取接口请求参数
        Object[] arguments = invocation.getArguments();
        // 判断接口请求参数数量
        if (arguments.length < 1) {
            return ipInvokers;
        }
        // 获取目标宿主机 IP 地址
        String targetAgentIp = (String) arguments[0];
        log.warn("[Target Host Ip] -> {}", targetAgentIp);

        // 循环进行路由匹配
        for (Invoker<T> invoker : invokers) {
            String providerIp = invoker.getUrl().getHost();
            log.warn("[CurrentIp] -> {}", providerIp);
            if (providerIp.equals(targetAgentIp)) {
                ipInvokers.add(invoker);
            }
        }
        return ipInvokers;
    }

}
