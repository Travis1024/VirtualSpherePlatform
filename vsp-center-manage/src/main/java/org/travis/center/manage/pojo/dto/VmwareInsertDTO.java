package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.ArchEnum;
import org.travis.center.common.enums.VmwareCreateFormEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName VmwareInsertDTO
 * @Description VmwareInsertDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
@Data
public class VmwareInsertDTO implements Serializable {

    @Schema(description="虚拟机名称")
    @NotBlank(message = "虚拟机名称不能为空!")
    private String name;

    @Schema(description="虚拟机描述信息")
    private String description;

    @Schema(description="虚拟机当前所属宿主机 ID")
    @NotNull(message = "虚拟机当前所属宿主机 ID 不能为空!")
    private Long hostId;

    @Schema(description="虚拟机创建形式（1-ISO安装介质、2-现有磁盘镜像）")
    @NotNull(message = "虚拟机创建形式不能为空!")
    private VmwareCreateFormEnum createForm;

    @Schema(description="虚拟机 vCPU 核数")
    @NotNull(message = "虚拟机 vCPU 核数不能为空!")
    private Integer vcpuCurrent;

    @Schema(description="虚拟机内存值（字节）")
    @NotNull(message = "虚拟机内存值不能为空!")
    private Long memoryCurrent;

    @Schema(description = "虚拟机 Image 镜像 ID")
    private Long imageId;

    @Schema(description = "虚拟机架构")
    @NotNull(message = "虚拟机架构不能为空!")
    private ArchEnum vmwareArch;

    @Schema(description = "虚拟机系统磁盘大小（单位: GB）")
    @NotNull(message = "虚拟机系统磁盘容量不能为空!")
    @Min(value = 10, message = "虚拟机系统磁盘容量最小为 10 GB")
    @Max(value = 1024, message = "虚拟机系统磁盘容量最大为 1024 GB")
    private Integer systemDiskSize;
}
