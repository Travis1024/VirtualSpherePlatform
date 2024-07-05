package org.travis.center.manage.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName SnapshotInsertDTO
 * @Description SnapshotInsertDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/5
 */
@Data
public class SnapshotInsertDTO implements Serializable {
    /**
     * 快照名称（用户定义）
     */
    @Schema(description="快照名称（用户定义）")
    @NotBlank(message = "快照名称不能为空!")
    private String snapshotName;

    /**
     * 所属虚拟机 ID
     */
    @Schema(description="所属虚拟机 ID")
    @NotNull(message = "所属虚拟机 ID 不能为空!")
    private Long vmwareId;

    /**
     * 快照描述信息
     */
    @Schema(description="快照描述信息")
    private String description;
}
