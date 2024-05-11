package org.travis.api.client.host;

/**
 * @ClassName HostCommandClient
 * @Description HostCommandClient
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/11
 */
public interface HostCommandClient {

    String execSingleCommand(String targetHostIp, String command);
}
