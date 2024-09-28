package org.travis.center.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @ClassName PipelineBusinessCodeEnum
 * @Description 「责任链」业务代码枚举类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/13
 */
@Getter
@ToString
@AllArgsConstructor
public enum PipelineBusinessCodeEnum {

    /**
     * 虚拟机快照创建业务
     */
    SNAPSHOT_CREATE("snapshot_create", "虚拟机快照创建业务"),
    /**
     * 虚拟机快照恢复业务
     */
    SNAPSHOT_RESUME("snapshot_resume", "虚拟机快照恢复业务")
    ;

    /**
     * code 关联着责任链的模板
     */
    private final String code;

    /**
     * 类型说明
     */
    private final String description;
}
