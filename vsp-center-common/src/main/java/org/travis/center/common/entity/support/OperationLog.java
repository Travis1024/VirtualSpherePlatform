package org.travis.center.common.entity.support;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.travis.center.common.enums.BusinessStateEnum;
import org.travis.center.common.enums.BusinessTypeEnum;

/**
 * @ClassName OperationLog
 * @Description OperationLog
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Schema
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_OPERATION_LOG")
public class OperationLog extends com.baomidou.mybatisplus.extension.activerecord.Model<OperationLog> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 操作状态码（0-未知、1-成功、2-失败）
     */
    @TableField(value = "OPERATION_STATE")
    @Schema(description="操作状态码（0-未知、1-成功、2-失败）")
    private BusinessStateEnum operationState;

    /**
     * 请求来源
     */
    @TableField(value = "IP_ADDRESS")
    @Schema(description="请求来源")
    private String ipAddress;

    /**
     * 请求 URL
     */
    @TableField(value = "REQUEST_URL")
    @Schema(description="请求 URL")
    private String requestUrl;

    /**
     * 操作用户
     */
    @TableField(value = "USER_ID")
    @Schema(description="操作用户")
    private Long userId;

    /**
     * 操作方法
     */
    @TableField(value = "\"METHOD\"")
    @Schema(description="操作方法")
    private String method;

    /**
     * 请求方法
     */
    @TableField(value = "REQUEST_METHOD")
    @Schema(description="请求方法")
    private String requestMethod;

    /**
     * 操作标题
     */
    @TableField(value = "TITLE")
    @Schema(description="操作标题")
    private String title;

    /**
     * 请求业务类型
     */
    @TableField(value = "BUSINESS_TYPE")
    @Schema(description="请求业务类型")
    private BusinessTypeEnum businessType;

    /**
     * 请求参数
     */
    @TableField(value = "REQUEST_PARAMS")
    @Schema(description="请求参数")
    private String requestParams;

    /**
     * 响应信息
     */
    @TableField(value = "RESPONSE_INFO")
    @Schema(description="响应信息")
    private String responseInfo;

    /**
     * 操作异常消息
     */
    @TableField(value = "ERROR_MESSAGE")
    @Schema(description="操作异常消息")
    private String errorMessage;

    /**
     * 消耗时间（毫秒）
     */
    @TableField(value = "COST_TIME")
    @Schema(description = "消耗时间（毫秒）")
    private Long costTime;

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

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_OPERATION_STATE = "OPERATION_STATE";

    public static final String COL_IP_ADDRESS = "IP_ADDRESS";

    public static final String COL_REQUEST_URL = "REQUEST_URL";

    public static final String COL_USER_ID = "USER_ID";

    public static final String COL_METHOD = "METHOD";

    public static final String COL_REQUEST_METHOD = "REQUEST_METHOD";

    public static final String COL_TITLE = "TITLE";

    public static final String COL_BUSINESS_TYPE = "BUSINESS_TYPE";

    public static final String COL_REQUEST_PARAMS = "REQUEST_PARAMS";

    public static final String COL_RESPONSE_INFO = "RESPONSE_INFO";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";

    public static final String COL_ERROR_MESSAGE = "ERROR_MESSAGE";

    public static final String COL_COST_TIME = "COST_TIME";
}
