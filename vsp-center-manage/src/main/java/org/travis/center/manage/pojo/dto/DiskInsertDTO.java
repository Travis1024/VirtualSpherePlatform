package org.travis.center.manage.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.DiskTypeEnum;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.*;
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
     * 磁盘名称
     */
    @Schema(description = "磁盘名称 (限制只能包含数字、字母、短横线)")
    @NotEmpty(message = "磁盘名称不能为空!")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "磁盘名称只能包含数字、字母、短横线!")
    private String name;

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
}
