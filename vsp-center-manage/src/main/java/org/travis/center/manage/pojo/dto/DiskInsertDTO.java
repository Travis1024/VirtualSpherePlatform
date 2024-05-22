package org.travis.center.manage.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.DiskTypeEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DiskInsertDTO
 * @Description DiskInsertDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/22
 */
@Data
public class DiskInsertDTO implements Serializable {
    /**
     * 磁盘描述信息
     */
    @Schema(description="磁盘描述信息")
    private String description;

    /**
     * 磁盘大小（字节）
     */
    @Schema(description="磁盘大小（字节）")
    @NotNull(message = "磁盘大小不能为空")
    @Min(value = 5L * 1024L * 1024L * 1024L, message = "磁盘大小不能小于-5G")
    private Long spaceSize;

    /**
     * 磁盘所属虚拟机ID
     */
    @Schema(description="磁盘所属虚拟机ID")
    @NotNull(message = "磁盘所属虚拟机ID不能为空!")
    private Long vmwareId;

    /**
     * 磁盘类型（1-Data、2-Root）
     */
    @Schema(description="磁盘类型（1-Data、2-Root）")
    @NotNull(message = "磁盘类型不能为空!")
    private DiskTypeEnum diskType;
}
