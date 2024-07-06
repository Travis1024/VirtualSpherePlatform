package org.travis.api.pojo.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DiskBasicInfoBO
 * @Description DiskBasicInfoBO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/6
 */
@Data
public class DiskBasicInfoBO implements Serializable {
    /**
     * 磁盘类型, eg: file
     */
    private String type;
    /**
     * 磁盘设备, eg: disk
     */
    private String device;
    /**
     * 磁盘挂载点, eg: vda
     */
    private String target;
    /**
     * 磁盘路径, eg: /var/lib/libvirt/images/kylin2.snap-4-manual
     */
    private String source;
}
