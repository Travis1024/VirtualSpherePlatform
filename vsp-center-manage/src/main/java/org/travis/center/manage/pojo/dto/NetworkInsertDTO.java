package org.travis.center.manage.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.support.aspect.RequestLockKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName NetworkInsertDTO
 * @Description NetworkInsertDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/24
 */
@Data
public class NetworkInsertDTO implements Serializable {
    /**
     * 网卡名称（eg：p4p1）
     */
    @RequestLockKey
    @Schema(description="网卡名称（eg：p4p1）", example = "p4p1")
    @NotBlank(message = "二层网络网卡名称不能为空!")
    private String nicName;

    /**
     * 网卡起始 IP 地址（192.168.0.0）
     */
    @Schema(description="网卡起始 IP 地址（192.168.0.0）", example = "192.168.0.0")
    @NotBlank(message = "二层网络网卡起始 IP 地址不能为空!")
    private String nicStartAddress;

    /**
     * 网卡掩码（eg：24）
     */
    @Schema(description="网卡掩码（eg：24）", example = "24")
    @NotNull(message = "网卡掩码不能为空!")
    private Integer nicMask;
}
