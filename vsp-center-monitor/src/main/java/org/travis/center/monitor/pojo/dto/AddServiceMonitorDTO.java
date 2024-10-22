package org.travis.center.monitor.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.travis.shared.common.enums.MachineTypeEnum;
import org.travis.shared.common.enums.ServiceControlTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName AddServiceMonitorDTO
 * @Description 新增服务监控 DTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/1/4
 */
@Data
public class AddServiceMonitorDTO implements Serializable {

    @Schema(description = "服务所属节点类型(1:宿主机、2:虚拟机)")
    @NotNull(message = "服务所属节点类型不能为空！")
    private MachineTypeEnum serviceMachineType;

    @Schema(description = "服务所属节点UUID")
    @NotBlank(message = "服务所属节点 UUID 不能为空！")
    private String serviceMachineUuid;

    @Schema(description = "当前服务名称")
    @NotBlank(message = "当前服务名称不能为空！")
    private String serviceName;

    @Schema(description = "预备替换服务名称")
    @NotBlank(message = "预备替换服务名称不能为空！")
    private String serviceReplaceName;

    @Schema(description = "服务 CPU 上限占用率")
    @NotNull(message = "服务 CPU 上限占用率不能为空！")
    @Range(min = 1, max = 100)
    private Integer serviceCpuLimitRate;

    @Schema(description = "服务 内存 上限占用率")
    @NotNull(message = "服务 内存 上限占用率不能为空！")
    @Range(min = 1, max = 100)
    private Integer serviceMemLimitRate;

    @Schema(description = "服务健康值分数下限值")
    @NotNull(message = "服务健康值分数下限值不能为空！")
    @Range(min = 1, max = 100)
    private Integer serviceHealthLimitScore;

    @Schema(description = "服务自动调整类型（1:半自动、2:自动）")
    @NotNull(message = "服务自动调整类型不能为空！")
    private ServiceControlTypeEnum serviceAutoType;

}
