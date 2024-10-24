package org.travis.center.monitor.pojo.regulate;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CacheMemStat
 * @Description 虚拟机内存状态数据
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/13
 */
@Data
public class CacheMemStat implements Serializable {
    private Double swapMemoryUsedPercent;
    private Double virtualMemoryUsedPercent;
    private Long timestamp;
}
