package org.travis.center.common.entity.monitor;

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
 * @ClassName ServiceMonitor
 * @Description ServiceMonitor
 * @Author Travis
 * @Data 2024/10
 */
/**
 * 服务质量监控记录表
 */
@Schema(description="服务质量监控记录表")
@Data
@EqualsAndHashCode(callSuper=true)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "VSP_SERVICE_MONITOR")
public class ServiceMonitor extends com.baomidou.mybatisplus.extension.activerecord.Model<ServiceMonitor> implements Serializable {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    @Schema(description="ID")
    private Long id;

    /**
     * 服务所属节点类型(1:宿主机、2:虚拟机)
     */
    @TableField(value = "SERVICE_MACHINE_TYPE")
    @Schema(description="服务所属节点类型(1:宿主机、2:虚拟机)")
    private Integer serviceMachineType;

    /**
     * 服务所属节点UUID
     */
    @TableField(value = "SERVICE_MACHINE_UUID")
    @Schema(description="服务所属节点UUID")
    private String serviceMachineUuid;

    /**
     * 当前服务名称
     */
    @TableField(value = "SERVICE_NAME")
    @Schema(description="当前服务名称")
    private String serviceName;

    /**
     * 预备替换服务名称
     */
    @TableField(value = "SERVICE_REPLACE_NAME")
    @Schema(description="预备替换服务名称")
    private String serviceReplaceName;

    /**
     * 当前服务进程PID
     */
    @TableField(value = "SERVICE_PID")
    @Schema(description="当前服务进程PID")
    private Integer servicePid;

    /**
     * 服务CPU上限占用率
     */
    @TableField(value = "SERVICE_CPU_LIMIT_RATE")
    @Schema(description="服务CPU上限占用率")
    private Integer serviceCpuLimitRate;

    /**
     * 服务内存上限占用率
     */
    @TableField(value = "SERVICE_MEM_LIMIT_RATE")
    @Schema(description="服务内存上限占用率")
    private Integer serviceMemLimitRate;

    /**
     * 服务健康值分数下限值
     */
    @TableField(value = "SERVICE_HEALTH_LIMIT_SCORE")
    @Schema(description="服务健康值分数下限值")
    private Integer serviceHealthLimitScore;

    /**
     * 服务自动调整类型（1:半自动、2:自动）
     */
    @TableField(value = "SERVICE_AUTO_TYPE")
    @Schema(description="服务自动调整类型（1:半自动、2:自动）")
    private Integer serviceAutoType;

    /**
     * 服务监控启用状态
     */
    @TableField(value = "SERVICE_STATE")
    @Schema(description="服务监控启用状态")
    private String serviceState;

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

    public static final String COL_ID = "ID";

    public static final String COL_SERVICE_MACHINE_TYPE = "SERVICE_MACHINE_TYPE";

    public static final String COL_SERVICE_MACHINE_UUID = "SERVICE_MACHINE_UUID";

    public static final String COL_SERVICE_NAME = "SERVICE_NAME";

    public static final String COL_SERVICE_REPLACE_NAME = "SERVICE_REPLACE_NAME";

    public static final String COL_SERVICE_PID = "SERVICE_PID";

    public static final String COL_SERVICE_CPU_LIMIT_RATE = "SERVICE_CPU_LIMIT_RATE";

    public static final String COL_SERVICE_MEM_LIMIT_RATE = "SERVICE_MEM_LIMIT_RATE";

    public static final String COL_SERVICE_HEALTH_LIMIT_SCORE = "SERVICE_HEALTH_LIMIT_SCORE";

    public static final String COL_SERVICE_AUTO_TYPE = "SERVICE_AUTO_TYPE";

    public static final String COL_SERVICE_STATE = "SERVICE_STATE";

    public static final String COL_IS_DELETED = "IS_DELETED";

    public static final String COL_UPDATER = "UPDATER";

    public static final String COL_CREATOR = "CREATOR";

    public static final String COL_UPDATE_TIME = "UPDATE_TIME";

    public static final String COL_CREATE_TIME = "CREATE_TIME";
}
