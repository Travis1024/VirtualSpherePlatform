package org.travis.center.manage.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName VmwareErrorVO
 * @Description VmwareErrorVO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Data
public class VmwareErrorVO implements Serializable {
    @Schema(description = "虚拟机-ID")
    private Long vmwareId;
    @Schema(description = "虚拟机操作错误信息")
    private String errorMessage;
}
