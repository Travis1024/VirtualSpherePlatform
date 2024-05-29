package org.travis.api.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName HostDetailsVO
 * @Description HostDetailsVO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Data
public class HostDetailsBO implements Serializable {

    @Schema(description = "主机架构信息")
    private String osArch;

    @Schema(description = "主机内存大小（字节）")
    private Long memoryTotal;

    @Schema(description = "主机 CPU 核数")
    private Integer cpuNum;

    @Schema(description = "虚拟 CPU 总核数")
    private Integer vCpuAllNum;

    @Schema(description = "虚拟 CPU 已定义核数")
    private Integer vCpuDefinitionNum;

    @Schema(description = "虚拟 CPU 活跃核数")
    private Integer vCpuActiveNum;
}
