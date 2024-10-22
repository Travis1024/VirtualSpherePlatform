package org.travis.center.monitor.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.enums.MachineTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName ManualServiceReplaceDTO
 * @Description 手动执行服务替换功能参数类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/1/4
 */
@Data
public class ManualServiceReplaceDTO implements Serializable {

    @Schema(description = "服务所属节点类型(1:宿主机、2:虚拟机)")
    @NotNull(message = "服务所属节点类型不能为空！")
    private MachineTypeEnum serviceMachineType;

    @RequestLockKey
    @Schema(description = "服务所属节点UUID")
    @NotBlank(message = "服务所属节点 UUID 不能为空！")
    private String serviceMachineUuid;

    @RequestLockKey
    @Schema(description = "当前服务名称")
    @NotBlank(message = "当前服务名称不能为空！")
    private String serviceName;

    @Schema(description = "预备替换服务名称")
    @NotBlank(message = "预备替换服务名称不能为空！")
    private String serviceReplaceName;
}
