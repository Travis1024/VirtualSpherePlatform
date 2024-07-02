package org.travis.center.support.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DynamicConfigUpdateDTO
 * @Description DynamicConfigUpdateDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Data
public class DynamicConfigUpdateDTO implements Serializable {
    /**
     * ID
     */
    @RequestLockKey
    @Schema(description="ID")
    @NotNull(message = "动态配置ID不能为空!")
    private Long id;

    /**
     * 动态配置 VALUE
     */
    @Schema(description="动态配置 VALUE (只接收字符串)")
    @NotBlank(message = "动态配置 VALUE 不能为空!")
    private String configValue;
}
