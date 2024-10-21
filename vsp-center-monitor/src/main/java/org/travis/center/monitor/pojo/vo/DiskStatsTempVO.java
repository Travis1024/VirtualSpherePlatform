package org.travis.center.monitor.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DiskStatsResultVO
 * @Description Disk 监测推送数据 VO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Data
public class DiskStatsTempVO implements Serializable {
    private String device;
    private String mountpoint;
    private String usedPercent;
}
