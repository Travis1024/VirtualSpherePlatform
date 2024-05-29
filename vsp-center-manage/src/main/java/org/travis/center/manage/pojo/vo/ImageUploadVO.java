package org.travis.center.manage.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ImageUploadVO
 * @Description ImageUploadVO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/14
 */
@Data
public class ImageUploadVO implements Serializable {
    @Schema(description = "文件共享存储服务器IP-Random")
    private String serverAgentIp;
    @Schema(description = "服务器应用端口")
    private String serverAgentPort;
    @Schema(description = "服务器文件上传请求URI")
    private String serverUploadUri;
    @Schema(description = "服务器文件合并请求URI")
    private String serverMergeUri;
    @Schema(description = "服务器预生成文件绝对路径")
    private List<String> serverTempFilePathList;
    @Schema(description = "服务器-Image-文件路径")
    private String serverFilePath;
    @Schema(description = "Image-镜像ID")
    private Long imageId;
}
