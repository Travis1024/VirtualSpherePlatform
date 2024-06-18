package org.travis.center.auth.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName AuthGroupUpdateDTO
 * @Description AuthGroupUpdateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Data
public class AuthGroupUpdateDTO implements Serializable {
    /**
     * ID
     */
    @RequestLockKey
    @Schema(description="ID")
    @NotNull(message = "权限组 ID 不能为空!")
    private Long id;

    /**
     * 权限组描述信息
     */
    @Schema(description="权限组描述信息")
    private String description;
}
