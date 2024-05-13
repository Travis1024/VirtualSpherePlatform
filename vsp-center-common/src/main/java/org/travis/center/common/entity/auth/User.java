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
import org.travis.center.common.enums.UserRoleEnum;

/**
 * @ClassName User
 * @Description User
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
/**
 * 用户信息表
 */
@Schema(description="用户信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_USER")
public class User extends com.baomidou.mybatisplus.extension.activerecord.Model<User> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 登录用户名
     */
    @TableField(value = "USERNAME")
    @Schema(description="登录用户名")
    private String username;

    /**
     * 登录密码
     */
    @TableField(value = "\"PASSWORD\"")
    @Schema(description="登录密码")
    private String password;

    /**
     * 用户手机号
     */
    @TableField(value = "PHONE")
    @Schema(description="用户手机号")
    private String phone;

    /**
     * 用户邮箱地址
     */
    @TableField(value = "EMAIL")
    @Schema(description="用户邮箱地址")
    private String email;

    /**
     * 用户真实名字
     */
    @TableField(value = "REAL_NAME")
    @Schema(description="用户真实名字")
    private String realName;

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

    /**
     * 用户角色类型（1-管理员、2-普通用户）
     */
    @TableField(value = "ROLE_TYPE")
    @Schema(description="用户角色类型（1-管理员、2-普通用户）")
    private UserRoleEnum roleType;

    /**
     * 用户个人介绍
     */
    @TableField(value = "DESCRIPTION")
    @Schema(description="用户个人介绍")
    private String description;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_USERNAME = "USERNAME";

    public static final String COL_PASSWORD = "PASSWORD";

    public static final String COL_PHONE = "PHONE";

    public static final String COL_EMAIL = "EMAIL";

    public static final String COL_REAL_NAME = "REAL_NAME";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";

    public static final String COL_ROLE_TYPE = "ROLE_TYPE";

    public static final String COL_DESCRIPTION = "DESCRIPTION";
}
