package org.travis.shared.common.constants;

/**
 * @ClassName SystemConstant
 * @Description 系统常量类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/8
 */
public class SystemConstant {
    /**
     * 请求头中的请求 ID 信息
     */
    public static final String REQUEST_ID_HEADER = "requestId";
    /**
     * 请求头中的用户 ID 信息
     */
    public static final String USER_ID_HEADER = "userId";

    /**
     * 数据库表字段 - 创建时间
     */
    public static final String DATA_FIELD_NAME_CREATE_TIME = "createTime";
    /**
     * 数据库表字段 - 更新时间
     */
    public static final String DATA_FIELD_NAME_UPDATE_TIME = "updateTime";
    /**
     * 数据库表字段 - 创建人（creator）
     */
    public static final String DATA_FIELD_NAME_CREATOR = "creator";
    /**
     * 数据库表字段 - 更新人（updater）
     */
    public static final String DATA_FIELD_NAME_UPDATER = "updater";
    /**
     * 数据库表字段 - 逻辑删除: 0-未删除、1-已删除（is_deleted）
     */
    public static final String DATA_FIELD_NAME_IS_DELETED = "isDeleted";
}
