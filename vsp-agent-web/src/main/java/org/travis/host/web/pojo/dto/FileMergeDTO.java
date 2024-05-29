package org.travis.host.web.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName FileMergeDTO
 * @Description FileMergeDTO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/18
 */
@Data
public class FileMergeDTO implements Serializable {
    @Schema(description = "Image-镜像ID")
    @NotNull(message = "Image-镜像ID不能为空!")
    private Long imageId;

    @Schema(description = "服务器-Image-文件路径")
    @NotEmpty(message = "Image-文件路径不能为空!")
    private String serverFilePath;

    @Schema(description = "完整文件-CRC32，用于合并后文件完整性校验")
    @NotBlank(message = "文件校验码不能为空!")
    private String crc32;

    @Schema(description = "服务器预生成文件绝对路径列表")
    @NotNull(message = "服务器预生成文件绝对路径列表不能为空!")
    private List<String> serverTempFilePathList;
}
