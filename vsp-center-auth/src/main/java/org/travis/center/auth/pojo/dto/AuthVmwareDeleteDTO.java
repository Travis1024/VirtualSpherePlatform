package org.travis.center.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName AuthVmwareDeleteDTO
 * @Description AuthVmwareDeleteDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Data
public class AuthVmwareDeleteDTO implements Serializable {
    @Schema(description = "权限组 ID")
    @NotNull(message = "权限组 ID 不能为空!")
    private Long authGroupId;

    @Schema(description = "虚拟机 ID 列表")
    @NotNull(message = "虚拟机 ID 列表不能为空!")
    private List<Long> vmwareIdList;
}
