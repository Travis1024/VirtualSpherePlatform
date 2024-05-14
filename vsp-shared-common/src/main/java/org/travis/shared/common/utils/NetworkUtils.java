package org.travis.shared.common.utils;

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
public class NetworkUtils {
    public static String getLocalHostAddress() throws UnknownHostException, SocketException {
        InetAddress candidateAddress = null;
        // 遍历所有的网络接口
        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
            NetworkInterface networkInterface = ifaces.nextElement();
            // 在所有的接口下再遍历 IP
            for (Enumeration<InetAddress> inetAddrs = networkInterface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                InetAddress inetAddr = inetAddrs.nextElement();
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
}
