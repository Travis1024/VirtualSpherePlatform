package org.travis.agent.web.pojo.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BridgeInitMessageBO
 * @Description BridgeInitMessageBO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/6
 */
@Data
public class BridgeInitMessageBO implements Serializable {
    private Long hostId;
    private Boolean isSuccess;
    private String stateMessage;
}
