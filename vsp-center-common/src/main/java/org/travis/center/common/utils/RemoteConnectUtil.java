package org.travis.center.common.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName HostConnectUtil
 * @Description 宿主机连接工具类（JSch）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/5/12
 */
@Slf4j
public class RemoteConnectUtil {
    /**
     * Key: host uuid; Value: session 对象
     * Key: host ip; Value: session 对象
     */
    private static final Map<String, Session> connectionMap = new HashMap<>();

    public static R<?> execCommand(String ip, String port, String loginUsername, String loginPassword, String command) {
        R<Session> hostSshSession = getRemoteSessionConnect(ip, port, loginUsername, loginPassword);
        if (hostSshSession.checkFail()) {
            return hostSshSession;
        }
        Session session = hostSshSession.getData();

        StringBuilder execResult = null;
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("export LANG=en_US.UTF-8 && " + command);
            InputStream inputStream = channel.getInputStream();
            channel.connect(2000);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (execResult == null) {
                    execResult = new StringBuilder(inputLine);
                } else {
                    execResult.append("\n").append(inputLine);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
            connectionMap.remove(ip);
            return R.error(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return R.ok(execResult != null ? execResult.toString() : "");
    }


    public static R<Session> getRemoteSessionConnect(String ip, String port, String loginUsername, String loginPassword) {
        // 判断 map 是否存在连接缓存
        if (connectionMap.containsKey(ip)) {
            Session session = connectionMap.get(ip);
            // 判断 session 是否可用
            if (session.isConnected()) return R.ok(session);
            else connectionMap.remove(ip);
        }
        JSch jSch = new JSch();
        Session session = null;
        try {
            session = jSch.getSession(loginUsername, ip, Integer.parseInt(port));
            session.setPassword(loginPassword);
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.setTimeout(300000);
            session.setServerAliveInterval(10000);
            session.connect(15000);
            connectionMap.put(ip, session);
            return R.ok(session);
        } catch (Exception e) {
            if (session != null) {
                session.disconnect();
            }
            connectionMap.remove(ip);
            log.error(e.toString());
            return R.error(BizCodeEnum.UNKNOW.getCode(), e.getMessage());
        }
    }
}
