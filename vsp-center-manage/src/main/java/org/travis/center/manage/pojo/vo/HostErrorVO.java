package org.travis.center.manage.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName HostErrorVO
 * @Description HostErrorVO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/6
 */
@Data
public class HostErrorVO implements Serializable {
    @Schema(description = "宿主机-ID")
    private Long hostId;
    @Schema(description = "宿主机操作错误信息")
    private String errorMessage;
}
