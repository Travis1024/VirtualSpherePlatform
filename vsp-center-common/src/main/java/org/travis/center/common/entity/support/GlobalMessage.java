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
import org.travis.shared.common.enums.MsgConfirmEnum;
import org.travis.shared.common.enums.MsgModuleEnum;
import org.travis.shared.common.enums.MsgStateEnum;

/**
 * @ClassName GlobalMessage
 * @Description GlobalMessage
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
@TableName(value = "VSP_GLOBAL_MESSAGE")
public class GlobalMessage extends com.baomidou.mybatisplus.extension.activerecord.Model<GlobalMessage> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 消息状态
     */
    @TableField(value = "MESSAGE_STATE")
    @Schema(description="消息状态")
    private MsgStateEnum messageState;

    /**
     * 消息所属模块
     */
    @TableField(value = "MESSAGE_MODULE")
    @Schema(description="消息所属模块")
    private MsgModuleEnum messageModule;

    /**
     * 消息内容
     */
    @TableField(value = "MESSAGE_CONTENT")
    @Schema(description="消息内容")
    private String messageContent;

    /**
     * 消息是否确认
     */
    @TableField(value = "IS_CONFIRM")
    @Schema(description="消息是否确认")
    private MsgConfirmEnum isConfirm;

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
     * 消息确认用户
     */
    @TableField(value = "CONFIRM_USER_ID")
    @Schema(description="消息确认用户")
    private Long confirmUserId;

    /**
     * 消息确认时间
     */
    @TableField(value = "CONFIRM_TIME")
    @Schema(description="消息确认时间")
    private Date confirmTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "ID";

    public static final String COL_MESSAGE_STATE = "MESSAGE_STATE";

    public static final String COL_MESSAGE_MODULE = "MESSAGE_MODULE";

    public static final String COL_MESSAGE_CONTENT = "MESSAGE_CONTENT";

    public static final String COL_IS_CONFIRM = "IS_CONFIRM";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";

    public static final String COL_CONFIRM_USER_ID = "CONFIRM_USER_ID";

    public static final String COL_CONFIRM_TIME = "CONFIRM_TIME";
}
