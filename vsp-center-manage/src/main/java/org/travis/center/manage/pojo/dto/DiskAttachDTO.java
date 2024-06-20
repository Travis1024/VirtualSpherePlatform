package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @ClassName DiskAttachDTO
 * @Description DiskAttachDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/21
 */
@Data
@AllArgsConstructor
public class DiskAttachDTO implements Serializable {

    /**
     * 磁盘ID
     */
    @RequestLockKey
    @Schema(description = "磁盘ID")
    @NotNull(message = "磁盘ID不能为空!")
    private Long diskId;

    /**
     * 目标虚拟机 ID
     */
    @Schema(description = "目标虚拟机 ID")
    @NotNull(message = "目标虚拟机 ID 不能为空!")
    private Long vmwareId;

    /**
     * 磁盘盘符 (eg:vda)
     */
    @Schema(description = "磁盘盘符 (eg:vda)")
    @NotEmpty(message = "磁盘盘符不能为空!")
    @Pattern(regexp = "^vd[a-z]$", message = "盘符必须以 'vd' 起始")
    private String targetDev;
}
