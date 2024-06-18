package org.travis.center.auth.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName AuthGroupInsertDTO
 * @Description AuthGroupInsertDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Data
public class AuthGroupInsertDTO implements Serializable {
    /**
     * 权限组名称
     */
    @RequestLockKey
    @Schema(description="权限组名称")
    @NotBlank(message = "权限组名称不能为空!")
    private String name;

    /**
     * 权限组描述信息
     */
    @Schema(description="权限组描述信息")
    private String description;
}
