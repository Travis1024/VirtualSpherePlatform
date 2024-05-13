package org.travis.center.common.entity.auth;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName AuthGroup
 * @Description AuthGroup
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
/**
 * 权限组信息表
 */
@Schema(description="权限组信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_AUTH_GROUP")
public class AuthGroup extends com.baomidou.mybatisplus.extension.activerecord.Model<AuthGroup> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 权限组名称
     */
    @TableField(value = "\"NAME\"")
    @Schema(description="权限组名称")
    private String name;

    /**
     * 权限组描述信息
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="权限组描述信息")
    private String description;

    /**
     * 逻辑信息
     */
    @TableField(value = "IS_DELETED")
    @Schema(description="逻辑信息")
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

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
