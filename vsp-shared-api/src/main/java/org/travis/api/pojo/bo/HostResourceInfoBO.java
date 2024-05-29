package org.travis.api.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName HostResourceInfoBO
 * @Description HostResourceInfoBO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Data
public class HostResourceInfoBO implements Serializable {

    @Schema(description = "主机总内存大小（内存+交换空间）（字节）")
    private Long memoryTotalMax;

    @Schema(description = "主机使用中内存大小（内存+交换空间）（字节）")
    private Long memoryTotalInUse;

    @Schema(description = "虚拟 CPU 总核数")
    private Integer vCpuAllNum;

    @Schema(description = "虚拟 CPU 已定义核数")
    private Integer vCpuDefinitionNum;

    @Schema(description = "虚拟 CPU 活跃核数")
    private Integer vCpuActiveNum;
}
