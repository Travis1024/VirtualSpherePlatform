package org.travis.center.common.entity.support;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @ClassName DynamicConfigInfo
 * @Description DynamicConfigInfo
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_DYNAMIC_CONFIG_INFO")
public class DynamicConfigInfo extends com.baomidou.mybatisplus.extension.activerecord.Model<DynamicConfigInfo> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 动态配置名称
     */
    @TableField(value = "CONFIG_NAME")
    @Schema(description="动态配置名称")
    private String configName;

    /**
     * 动态配置 KEY
     */
    @TableField(value = "CONFIG_KEY")
    @Schema(description="动态配置 KEY")
    private String configKey;

    /**
     * 动态配置 VALUE
     */
    @TableField(value = "CONFIG_VALUE")
    @Schema(description="动态配置 VALUE")
    private String configValue;

    /**
     * 动态配置描述信息
     */
    @TableField(value = "CONFIG_DESCRIPTION")
    @Schema(description="动态配置描述信息")
    private String configDescription;

    /**
     * 配置类型
     */
    @TableField(value = "CONFIG_TYPE")
    @Schema(description="配置类型")
    private Integer configType;

    /**
     * 逻辑删除
     */
    @TableField(value = "IS_DELETED")
    @Schema(description="逻辑删除")
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

    /**
     * 动态配置示例值
     */
    @TableField(value = "CONFIG_EXAMPLE")
    @Schema(description="动态配置示例值")
    private String configExample;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_CONFIG_NAME = "CONFIG_NAME";

    public static final String COL_CONFIG_KEY = "CONFIG_KEY";

    public static final String COL_CONFIG_VALUE = "CONFIG_VALUE";

    public static final String COL_CONFIG_DESCRIPTION = "CONFIG_DESCRIPTION";

    public static final String COL_CONFIG_TYPE = "CONFIG_TYPE";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";

    public static final String COL_CONFIG_EXAMPLE = "CONFIG_EXAMPLE";
}
