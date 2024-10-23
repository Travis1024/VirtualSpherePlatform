package org.travis.center.support.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TriggerInfoVO
 * @Description 报警触发事件返回信息 VO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Data
public class TriggerInfoVO implements Serializable {
    private String uuid;
    private Long timestamp;
    private String key;
    private String value;
}
