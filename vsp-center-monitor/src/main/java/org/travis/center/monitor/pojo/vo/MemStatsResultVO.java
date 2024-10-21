package org.travis.center.monitor.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MemStatsResultVO
 * @Description Memory 监测推送数据 VO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Data
public class MemStatsResultVO implements Serializable {
    private String swapMemoryUsedPercent;
    private String virtualMemoryUsedPercent;
    private Long timestamp;
}
