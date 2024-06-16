package org.travis.api.pojo.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName HostHealthyStateBO
 * @Description HostHealthyStateBO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Data
public class HostHealthyStateBO implements Serializable {
    private String hostIp;
    private Long recordTime;
    private Map<String, String> vmwareUuidStateMap;
}
