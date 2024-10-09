package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.shared.common.exceptions.BadRequestException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName VmwareMigrateDTO
 * @Description VmwareMigrateDTO
 * @Author Travis
 * @Data 2024/10
 */
@Data
public class VmwareMigrateDTO implements Serializable {

    @RequestLockKey
    @Schema(description = "待迁移虚拟机ID")
    @NotNull(message = "待迁移虚拟机ID不能为空!")
    private Long vmwareId;

    @Schema(description = "目标宿主机ID")
    @NotNull(message = "目标宿主机ID不能为空!")
    private Long targetHostId;

    public void valid() {
        if (this.getVmwareId() == null) {
            throw new BadRequestException("虚拟机ID不能为空!");
        }
        if (this.getTargetHostId() == null) {
            throw new BadRequestException("目标宿主机ID不能为空!");
        }
    }
}
