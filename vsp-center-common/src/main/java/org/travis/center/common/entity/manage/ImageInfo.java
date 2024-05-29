package org.travis.center.common.entity.manage;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.travis.center.common.enums.ArchEnum;
import org.travis.center.common.enums.ImagePlatformEnum;
import org.travis.center.common.enums.ImageStateEnum;
import org.travis.center.common.enums.ImageTypeEnum;

/**
 * @ClassName ImageInfo
 * @Description ImageInfo
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_IMAGE_INFO")
public class ImageInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<ImageInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 镜像名字
     */
    @TableField(value = "\"NAME\"")
    @Schema(description="镜像名字")
    private String name;

    /**
     * 镜像描述信息
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="镜像描述信息")
    private String description;

    /**
     * 镜像 CPU 架构（1-x86_64、2-aarch64）
     */
    @TableField(value = "ARCHITECTURE")
    @Schema(description="镜像 CPU 架构（1-x86_64、2-aarch64）")
    private ArchEnum architecture;

    /**
     * 镜像存储路径（共享存储子路径）
     */
    @TableField(value = "SUB_PATH")
    @Schema(description="镜像存储路径（共享存储子路径）")
    private String subPath;

    /**
     * 镜像平台（0-Other、1-Linux、2-Windows）
     */
    @TableField(value = "IMAGE_PLATFORM")
    @Schema(description="镜像平台（0-Other、1-Linux、2-Windows）")
    private ImagePlatformEnum imagePlatform;

    /**
     * 镜像类型（1-ISO镜像、2-系统镜像）
     */
    @TableField(value = "IMAGE_TYPE")
    @Schema(description = "镜像类型（1-ISO镜像、2-系统镜像）")
    private ImageTypeEnum imageType;

    /**
     * 镜像状态（0-上传中、1-异常、2-就绪）
     */
    @TableField(value = "STATE")
    @Schema(description = "镜像状态（0-上传中、1-异常、2-就绪）")
    private ImageStateEnum state;

    /**
     * 镜像状态消息
     */
    @TableField(value = "STATE_MESSAGE")
    @Schema(description = "镜像状态消息")
    private String stateMessage;

    /**
     * 逻辑删除
     */
    @TableField(value = "IS_DELETED")
    @Schema(description="逻辑删除")
    @TableLogic
    private Integer isDeleted;

    /**
     * 更新者
     */
    @TableField(value = "UPDATER")
    @Schema(description="更新者")
    private Long updater;

    /**
     * 创建者
     */
    @TableField(value = "CREATOR")
    @Schema(description="创建者")
    private Long creator;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    @Schema(description="更新时间")
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    @Schema(description="创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_NAME = "NAME";

    public static final String COL_DESCRIPTION = "DESCRIPTION";

    public static final String COL_ARCHITECTURE = "ARCHITECTURE";

    public static final String COL_SUB_PATH = "SUB_PATH";

    public static final String COL_LOAD_URL = "LOAD_URL";

    public static final String COL_IMAGE_PLATFORM = "IMAGE_PLATFORM";

    public static final String COL_IMAGE_TYPE = "IMAGE_TYPE";

    public static final String COL_STATE = "STATE";

    public static final String COL_STATE_MESSAGE = "STATE_MESSAGE";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
