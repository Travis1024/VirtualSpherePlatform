package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.shared.common.domain.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DiskPageSelectByVmwareDTO
 * @Description DiskPageSelectByVmwareDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/23
 */
@Data
public class DiskPageSelectByVmwareDTO implements Serializable {

    @Valid
    @NotNull(message = "分页查询对象不能为空!")
    @Schema(description = "分页查询对象")
    private PageQuery pageQuery;

    @Schema(description = "虚拟机 ID")
    @NotNull(message = "虚拟机 ID 不能为空!")
    private Long vmwareId;
}
