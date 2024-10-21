package org.travis.center.monitor.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DiskStatsResultVO
 * @Description Disk 数据监测推送数据 VO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/7
 */
@Data
@AllArgsConstructor
public class DiskStatsResultVO {
    private List<DiskStatsTempVO> list;
    private Long timestamp;
}
