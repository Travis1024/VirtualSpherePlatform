package org.travis.shared.common.utils;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @ClassName NetworkUtils
 * @Description NetworkUtils
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
public class NetworkUtil {

    @Deprecated
    public static String getLocalHostAddress() throws UnknownHostException, SocketException {
        InetAddress candidateAddress = null;
        // 遍历所有的网络接口
        for (Enumeration<NetworkInterface> oneInterface = NetworkInterface.getNetworkInterfaces(); oneInterface.hasMoreElements(); ) {
            NetworkInterface networkInterface = oneInterface.nextElement();
            // 在所有的接口下再遍历 IP
            for (Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses(); inetAddress.hasMoreElements(); ) {
                InetAddress inetAddr = inetAddress.nextElement();
                // 排除回环地址
                if (!inetAddr.isLoopbackAddress()) {
                    if (inetAddr.isSiteLocalAddress()) {
                        // 如果是 site-local 地址，就是它了
                        return inetAddr.getHostAddress();
                    } else if (candidateAddress == null) {
                        // site-local 类型的地址未被发现，会选用其他非环回地址
                        candidateAddress = inetAddr;
                    }
                }
            }
        }
        if (candidateAddress != null) {
            return candidateAddress.getHostAddress();
        }
        // 如果没有其他地址，那么使用本地环回地址
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获取客户端IP
     *
     * @return IP地址
     */
    public static String getIpAddr() {
        return getIpAddr(ServletUtil.getRequest());
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非 unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return StrUtil.sub(ip, 0, 255);
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }
}
