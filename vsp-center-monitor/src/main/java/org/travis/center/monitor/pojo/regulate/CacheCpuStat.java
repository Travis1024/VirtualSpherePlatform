package org.travis.center.monitor.pojo.regulate;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName CacheCpuStat
 * @Description 虚拟机 CPU 状态数据
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/13
 */
@Data
public class CacheCpuStat implements Serializable {
    private List<Double> cpuPercents;
    private Long timestamp;
}
