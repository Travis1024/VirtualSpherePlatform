package org.travis.api.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName HostInsertToAgentDTO
 * @Description HostInsertToAgentDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Data
public class HostBridgedAdapterToAgentDTO implements Serializable {
    /**
     * 宿主机 ID
     */
    @Schema(description="宿主机 ID")
    private Long id;

    /**
     * 网卡名称（eg：p4p1）
     */
    @Schema(description="网卡名称（eg：p4p1）", example = "p4p1")
    private String nicName;

    /**
     * 网卡起始 IP 地址（192.168.0.0）
     */
    @Schema(description="网卡起始 IP 地址（192.168.0.0）", example = "192.168.0.0")
    private String nicStartAddress;

    /**
     * 网卡掩码（eg：24）
     */
    @Schema(description="网卡掩码（eg：24）", example = "24")
    private Integer nicMask;
}
