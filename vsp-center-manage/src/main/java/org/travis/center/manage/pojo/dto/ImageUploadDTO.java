package org.travis.center.manage.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.travis.center.common.enums.ArchEnum;
import org.travis.center.common.enums.ImagePlatformEnum;
import org.travis.center.common.enums.ImageTypeEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName ImageUploadDTO
 * @Description ImageUploadDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Data
public class ImageUploadDTO implements Serializable {
    /**
     * 镜像名字
     */
    @Schema(description="镜像名字")
    @NotEmpty(message = "镜像名字不能为空!")
    private String name;

    /**
     * 镜像描述信息
     */
    @Schema(description="镜像描述信息")
    private String description;

    /**
     * 镜像 CPU 架构（1-x86_64、2-aarch64）
     */
    @Schema(description="镜像 CPU 架构（1-x86_64、2-aarch64）")
    @NotNull(message = "镜像 CPU 架构不能为空!")
    private ArchEnum architecture;

    /**
     * 镜像平台（0-Other、1-Linux、2-Windows）
     */
    @Schema(description="镜像平台（0-Other、1-Linux、2-Windows）")
    @NotNull(message = "镜像平台类型不能为空!")
    private ImagePlatformEnum imagePlatform;

    /**
     * 镜像类型（1-ISO镜像、2-系统镜像）
     */
    @Schema(description = "镜像类型（1-ISO镜像、2-系统镜像）")
    @NotNull(message = "镜像类型不能为空!")
    private ImageTypeEnum imageType;

    /**
     * 镜像文件切片数量
     */
    @Schema(description = "镜像文件切片数量")
    @NotNull(message = "镜像文件切片数量不能为空!")
    private Integer sliceNumber;

    /**
     * 镜像源文件名称(包含后缀)
     */
    @Schema(description = "镜像源文件名称(包含后缀)")
    @NotEmpty(message = "镜像源文件名称不能为空!")
    private String imageFileName;
}
