package org.travis.center.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName VmwareInitializeDataEnum
 * @Description 虚拟机初始化数据枚举类
 * @Author Travis
 * @Data 2024/10
 */
@Getter
@AllArgsConstructor
public enum VmwareInitializeDataEnum {
    /**
     * 基础数据
     */
    VMWARE_BASIC_DATA_MONITOR_PERIOD(
            "虚拟机数据监控周期",
            "vmware.basic.data.monitor.period",
            10,
            Integer.class,
            "[UNIT-SECOND] 虚拟机数据监控周期（默认：10 秒）"
    ),

    VMWARE_BASIC_REGULATE_MIN_TIME_INTERVAL(
            "虚拟机调控最小时间间隔",
            "vmware.basic.regulate.min.time.interval",
            180,
            Integer.class,
            "[UNIT-SECOND] 虚拟机调控「配置自动更新」或「迁移」最小时间间隔（默认：180 秒，三分钟内同一虚拟机同一资源只自动修改一次配置）"
    ),

    VMWARE_BASIC_REGULATE_RECOMMEND_MESSAGE_MIN_TIME_INTERVAL(
            "虚拟机推荐消息最小时间间隔",
            "vmware.basic.regulate.recommend.message.min.time.interval",
            180,
            Integer.class,
            "[UNIT-SECOND] 虚拟机调控「推荐消息」最小时间间隔（默认：180 秒，三分钟内同一虚拟机同一资源只发送一次推荐信息）"
    ),

    VMWARE_BASIC_RESOURCE_PRIORITY_TYPE(
            "虚拟机资源优先级类型",
            "vmware.basic.resource.priority.type",
            VmwareResourcePriorityEnum.LOW.name(),
            String.class,
            "虚拟机资源优先级类型, 可选值:[HIGH, MIDDLE, LOW]"
    ),

    VMWARE_BASIC_REGULATE_STRATEGY_TYPE(
            "虚拟机调控策略类型",
            "vmware.basic.regulate.strategy.type",
            VmwareRegulateStrategyEnum.MANUAL.name(),
            String.class,
            "虚拟机调控策略类型, 可选值:[AUTOMATIC、SEMI_AUTOMATIC、MANUAL]"
    ),

    /**
     * CPU 数据
     */
    VMWARE_THRESHOLD_CPU_HIGH_VALUE(
            "虚拟机 CPU 高使用率监测值上限",
            "vmware.threshold.cpu.high.value",
            90,
            Integer.class,
            "[UNIT-RATE*100] 虚拟机 CPU 使用率「高」阈值（默认：90, 即 90%）"
    ),

    VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE(
            "虚拟机 CPU 高使用率监测时间范围",
            "vmware.threshold.cpu.high.time.range",
            120,
            Integer.class,
            "[UNIT-SECOND] 虚拟机 CPU 使用率「高」阈值统计时间（默认：120 秒）"
    ),

    VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE_RATE(
            "虚拟机 CPU 高使用率时间范围内占比触发阈值",
            "vmware.threshold.cpu.high.time.range.rate",
            0.8,
            Double.class,
            "[UNIT-RATE] 虚拟机 CPU 使用率「高」阈值统计时间「比例」（默认：0.8，即超过 80%）"
    ),

    VMWARE_THRESHOLD_CPU_LOW_VALUE(
            "虚拟机 CPU 低使用率监测值下限",
            "vmware.threshold.cpu.low.value",
            5,
            Integer.class,
            "[UNIT-RATE*100] 虚拟机 CPU 使用率「低」阈值（默认：5, 即 5%）"
    ),

    VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE(
            "虚拟机 CPU 低使用率监测时间范围",
            "vmware.threshold.cpu.low.time.range",
            300,
            Integer.class,
            "[UNIT-SECOND] 虚拟机 CPU 使用率「低」阈值统计时间（默认：300 秒）"
    ),

    VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE_RATE(
            "虚拟机 CPU 低使用率时间范围内占比触发阈值",
            "vmware.threshold.cpu.low.time.range.rate",
            0.9,
            Double.class,
            "[UNIT-RATE] 虚拟机 CPU 使用率「低」阈值统计时间「比例」（默认：0.9，即超过 90%）"
    ),

    /**
     * MEMORY 数据
     */
    VMWARE_THRESHOLD_MEMORY_HIGH_VALUE(
            "虚拟机 MEM 高使用率监测值上限",
            "vmware.threshold.memory.high.value",
            90,
            Integer.class,
            "[UNIT-RATE*100] 虚拟机 内存使用率「高」阈值（默认：90, 即 90%）"
    ),

    VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE(
            "虚拟机 MEM 高使用率监测时间范围",
            "vmware.threshold.memory.high.time.range",
            120,
            Integer.class,
            "[UNIT-SECOND] 虚拟机 内存使用率「高」阈值统计时间（默认：120 秒）"
    ),

    VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE_RATE(
            "虚拟机 MEM 高使用率时间范围内占比触发阈值",
            "vmware.threshold.memory.high.time.range.rate",
            0.9,
            Double.class,
            "[UNIT-RATE] 虚拟机 内存使用率「高」阈值统计时间「比例」（默认：0.9，即超过 90%）"
    ),

    VMWARE_THRESHOLD_MEMORY_LOW_VALUE(
            "虚拟机 MEM 低使用率监测值下限",
            "vmware.threshold.memory.low.value",
            5,
            Integer.class,
            "[UNIT-RATE*100] 虚拟机 内存使用率「低」阈值（默认：5, 即 5%）"
    ),

    VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE(
            "虚拟机 MEM 低使用率监测时间范围",
            "vmware.threshold.memory.low.time.range",
            300,
            Integer.class,
            "[UNIT-SECOND] 虚拟机 内存使用率「低」阈值统计时间（默认：300 秒）"
    ),

    VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE_RATE(
            "虚拟机 MEM 低使用率时间范围内占比触发阈值",
            "vmware.threshold.memory.low.time.range.rate",
            0.9,
            Double.class,
            "[UNIT-RATE] 虚拟机 内存使用率「低」阈值统计时间「比例」（默认：0.9，即超过 90%）"
    ),
    ;

    private final String name;
    private final String key;
    private final Object value;
    private final Class<?> typeClass;
    private final String desc;
}
