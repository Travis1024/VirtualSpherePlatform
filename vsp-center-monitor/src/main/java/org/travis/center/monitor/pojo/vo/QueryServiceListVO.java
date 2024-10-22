package org.travis.center.monitor.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName QueryServiceListVO
 * @Description 查询服务列表返回 VO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/1/4
 */
@Data
public class QueryServiceListVO implements Serializable {
    private String serviceName;
    private String serviceLoad;
    private String serviceActive;
    private String serviceSub;
    private String serviceDescription;
}
