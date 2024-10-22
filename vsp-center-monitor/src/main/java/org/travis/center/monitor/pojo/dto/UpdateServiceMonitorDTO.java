package org.travis.center.monitor.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.enums.ServiceControlTypeEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName UpdateServiceMonitorDTO
 * @Description 更新 ServiceMonitor DTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/1/4
 */
@Data
public class UpdateServiceMonitorDTO implements Serializable {
    @RequestLockKey
    @Schema(description = "服务监控记录ID")
    @NotNull(message = "服务监控记录ID不能为空！")
    private Long id;

    @Schema(description = "预备替换服务名称")
    private String serviceReplaceName;

    @Schema(description = "当前服务进程ID")
    private Integer servicePid;

    @Schema(description = "服务 CPU 上限占用率")
    @Range(min = 1, max = 100)
    private Integer serviceCpuLimitRate;

    @Schema(description = "服务 内存 上限占用率")
    @Range(min = 1, max = 100)
    private Integer serviceMemLimitRate;

    @Schema(description = "服务健康值分数下限值")
    @Range(min = 1, max = 100)
    private Integer serviceHealthLimitScore;

    @Schema(description = "服务自动调整类型（1:半自动、2:自动）")
    private ServiceControlTypeEnum serviceAutoType;
}
