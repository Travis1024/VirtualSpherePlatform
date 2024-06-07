package org.travis.agent.web.pojo.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BridgeInitResultMessageBO
 * @Description BridgeInitResultMessageBO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/6
 */
@Data
public class BridgeInitResultMessageBO implements Serializable {
    private Long hostId;
    private Boolean isSuccess;
    private String stateMessage;
}
