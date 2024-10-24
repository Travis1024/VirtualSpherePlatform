package org.travis.center.web.jobs;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigAffiliationTypeEnum;
import org.travis.center.common.enums.VmwareRegulateStrategyEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.manage.service.ResourceAllocationService;
import org.travis.center.monitor.pojo.regulate.*;
import org.travis.shared.common.constants.MonitorConstant;
import org.travis.shared.common.constants.VmwareRegulateConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.center.common.enums.VmwareInitializeDataEnum;
import org.travis.shared.common.exceptions.CommonException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @ClassName QuartzDataMonitorRegulateTaskJob
 * @Description QuartzDataMonitorRegulateTaskJob
 * @Author Travis
 * @Data 2024/10
 */
@Slf4j
@Component
public class QuartzDataMonitorRegulateTaskJob extends QuartzJobBean {

    @Resource
    private VmwareInfoMapper vmwareInfoMapper;
    @Resource
    private DynamicConfigInfoMapper dynamicConfigInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ResourceAllocationService resourceAllocationService;

    public void processCpu(VmwareInfo vmwareInfo, VmwareBasicThreshold vmwareBasicThreshold, VmwareCpuThreshold vmwareCpuThreshold) {
        log.info("*********** [CPU] ***********");
        // 1. 获取虚拟机 CPU 数据
        Object cpuData = redisTemplate.opsForList().index(MonitorConstant.CACHE_CPU + MonitorConstant.M + vmwareInfo.getUuid(), 0);
        if (null == cpuData) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Get CPU Data Fail, CPU Data is Empty.");
        }

        // 2.解析虚拟机 CPU 数据
        CacheCpuStat cacheCpuStat = JSONUtil.toBean(cpuData.toString(), CacheCpuStat.class);
        if (null == cacheCpuStat) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Parse CPU Data Fail.");
        }

        // 3.预处理 CPU 相关数据
        // CPU 平均占用率
        double cpuSum = cacheCpuStat.getCpuPercents().stream().reduce(Double::sum).orElseThrow(() -> new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Calc CPU Average Value Fail."));
        double cpuAverageValue = cpuSum / cacheCpuStat.getCpuPercents().size();
        log.info("CPU Average Value: {}", cpuAverageValue);

        // 4.CPU 调控策略执行
        if (cpuAverageValue > vmwareCpuThreshold.getThresholdCpuHighValue()) {
            log.info("CPU Average Value is High.");
            processCpuHigh(vmwareInfo, vmwareBasicThreshold, vmwareCpuThreshold);
        } else if (cpuAverageValue < vmwareCpuThreshold.getThresholdCpuLowValue()) {
            log.info("CPU Average Value is Low.");
            processCpuLow(vmwareInfo, vmwareBasicThreshold, vmwareCpuThreshold);
        } else {
            log.info("CPU Stat is Healthy.");
        }
    }

    private void processCpuHigh(VmwareInfo vmwareInfo, VmwareBasicThreshold vmwareBasicThreshold, VmwareCpuThreshold vmwareCpuThreshold) {
        // 1.记录当前时间戳
        long currentTimestamp = DateUtil.current();
        // 2.缓存当前 CPU 高位触发记录
        redisTemplate.opsForZSet().add(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.CPU_HIGH + vmwareInfo.getUuid(), currentTimestamp, currentTimestamp);
        // 3.清除监测时间范围外的历史记录
        redisTemplate.opsForZSet().removeRangeByScore(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.CPU_HIGH + vmwareInfo.getUuid(), 0, currentTimestamp - vmwareCpuThreshold.getThresholdCpuHighTimeRange() * 1000);
        // 4.获取监测时间范围内的高位触发数量
        Long count = redisTemplate.opsForZSet().size(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.CPU_HIGH + vmwareInfo.getUuid());
        if (null == count) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Get CPU High Trigger Count Fail.");
        }
        // 5.计算时间范围内高位触发率
        double currentRate = 1.0 * count * vmwareBasicThreshold.getBasicDataMonitorPeriod() / vmwareCpuThreshold.getThresholdCpuHighTimeRange();
        log.info("CPU High Trigger Count: {}, Current Rate: {}", count, currentRate);
        // 6.判断时间范围内高位触发率是否满足触发条件
        if (currentRate <= vmwareCpuThreshold.getThresholdCpuHighTimeRangeRate()) {
            log.info("CPU High Trigger Rate Threshold is Not Reached.");
            return;
        }
        // 7.触发「CPU资源扩充」或「CPU资源推荐」
        if (VmwareRegulateStrategyEnum.AUTOMATIC.name().equals(vmwareBasicThreshold.getBasicRegulateStrategyType())) {
            log.info("CPU High Trigger Rate Threshold is Reached, Trigger CPU Resource Expansion.");
            R<?> expanded = resourceAllocationService.expandCpuResource(vmwareInfo.getUuid(), true);
            if (expanded.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Expand CPU Resource Fail.");
            }
        } else {
            log.info("CPU High Trigger Rate Threshold is Reached, Trigger CPU Resource Recommend.");
            R<?> recommended = resourceAllocationService.recommendCpuResource(vmwareInfo.getUuid(), false);
            if (recommended.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Recommend CPU Resource Fail.");
            }
        }
    }

    private void processCpuLow(VmwareInfo vmwareInfo, VmwareBasicThreshold vmwareBasicThreshold, VmwareCpuThreshold vmwareCpuThreshold) {
        // 1.记录当前时间戳
        long currentTimestamp = DateUtil.current();
        // 2.缓存当前 CPU 低位触发记录
        redisTemplate.opsForZSet().add(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.CPU_LOW + vmwareInfo.getUuid(), currentTimestamp, currentTimestamp);
        // 3.清除监测时间范围外的历史记录
        redisTemplate.opsForZSet().removeRangeByScore(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.CPU_LOW + vmwareInfo.getUuid(), 0, currentTimestamp - vmwareCpuThreshold.getThresholdCpuLowTimeRange() * 1000);
        // 4.获取监测时间范围内的低位触发数量
        Long count = redisTemplate.opsForZSet().size(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.CPU_LOW + vmwareInfo.getUuid());
        if (null == count) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Get CPU Low Trigger Count Fail.");
        }
        // 5.计算时间范围内低位触发率
        double currentRate = 1.0 * count * vmwareBasicThreshold.getBasicDataMonitorPeriod() / vmwareCpuThreshold.getThresholdCpuLowTimeRange();
        log.info("CPU Low Trigger Count: {}, Current Rate: {}", count, currentRate);
        // 6.判断时间范围内低位触发率是否满足触发条件
        if (currentRate >= vmwareCpuThreshold.getThresholdCpuLowTimeRangeRate()) {
            log.info("CPU Low Trigger Rate Threshold is Not Reached.");
            return;
        }
        // 7.触发「CPU资源缩减」或「CPU资源推荐」
        if (VmwareRegulateStrategyEnum.AUTOMATIC.name().equals(vmwareBasicThreshold.getBasicRegulateStrategyType())) {
            log.info("CPU Low Trigger Rate Threshold is Reached, Trigger CPU Resource Reduce.");
            R<?> reduced = resourceAllocationService.reduceCpuResource(vmwareInfo.getUuid(), true);
            if (reduced.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Reduce CPU Resource Fail.");
            }
        } else {
            log.info("CPU Low Trigger Rate Threshold is Reached, Trigger CPU Resource Recommend.");
            R<?> recommended = resourceAllocationService.recommendCpuResource(vmwareInfo.getUuid(), true);
            if (recommended.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Recommend CPU Resource Fail.");
            }
        }
    }

    public void processMemory(VmwareInfo vmwareInfo, VmwareBasicThreshold vmwareBasicThreshold, VmwareMemoryThreshold vmwareMemoryThreshold) {
        log.info("*********** [MEM] ***********");
        // 1. 获取虚拟机 MEM 数据
        Object memData = redisTemplate.opsForList().index(MonitorConstant.CACHE_MEM + MonitorConstant.M + vmwareInfo.getUuid(), 0);
        if (null == memData) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Get MEM Data Fail, MEM Data is Empty.");
        }
        // 2.解析虚拟机 MEM 数据
        CacheMemStat cacheMemStat = JSONUtil.toBean(memData.toString(), CacheMemStat.class);
        if (null == cacheMemStat) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Parse MEM Data Fail.");
        }
        // 3.预处理 MEM 相关数据
        // MEM 平均占用率
        double memAverageValue = cacheMemStat.getVirtualMemoryUsedPercent();

        // 4.MEM 调控策略执行
        if (memAverageValue > vmwareMemoryThreshold.getThresholdMemoryHighValue()) {
            log.info("MEM Average Value is High.");
            processMemoryHigh(vmwareInfo, vmwareBasicThreshold, vmwareMemoryThreshold);
        } else if (memAverageValue < vmwareMemoryThreshold.getThresholdMemoryLowValue()) {
            log.info("MEM Average Value is Low.");
            processMemoryLow(vmwareInfo, vmwareBasicThreshold, vmwareMemoryThreshold);
        } else {
            log.info("MEM Stat is Healthy.");
        }
    }

    private void processMemoryHigh(VmwareInfo vmwareInfo, VmwareBasicThreshold vmwareBasicThreshold, VmwareMemoryThreshold vmwareMemoryThreshold) {
        // 1.记录当前时间戳
        long currentTimestamp = DateUtil.current();
        // 2.缓存当前 MEM 高位触发记录
        redisTemplate.opsForZSet().add(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.MEMORY_HIGH + vmwareInfo.getUuid(), currentTimestamp, currentTimestamp);
        // 3.清除监测时间范围外的历史记录
        redisTemplate.opsForZSet().removeRangeByScore(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.MEMORY_HIGH + vmwareInfo.getUuid(), 0, currentTimestamp - vmwareMemoryThreshold.getThresholdMemoryHighTimeRange() * 1000);
        // 4.获取监测时间范围内的高位触发数量
        Long count = redisTemplate.opsForZSet().size(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.MEMORY_HIGH + vmwareInfo.getUuid());
        if (null == count) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Get Memory High Trigger Count Fail.");
        }
        // 5.计算时间范围内高位触发率
        double currentRate = 1.0 * count * vmwareBasicThreshold.getBasicDataMonitorPeriod() / vmwareMemoryThreshold.getThresholdMemoryHighTimeRange();
        log.info("Memory High Trigger Count: {}, Current Rate: {}", count, currentRate);
        // 6.判断时间范围内高位触发率是否满足触发条件
        if (currentRate <= vmwareMemoryThreshold.getThresholdMemoryHighTimeRangeRate()) {
            log.info("Memory High Trigger Rate Threshold is Not Reached.");
            return;
        }
        // 7.触发「MEM资源缩减」或「MEM资源推荐」
        if (VmwareRegulateStrategyEnum.AUTOMATIC.name().equals(vmwareBasicThreshold.getBasicRegulateStrategyType())) {
            log.info("Memory High Trigger Rate Threshold is Reached, Trigger Memory Resource Reduce.");
            R<?> reduced = resourceAllocationService.reduceMemoryResource(vmwareInfo.getUuid(), true);
            if (reduced.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Reduce Memory Resource Fail.");
            }
        } else {
            log.info("Memory High Trigger Rate Threshold is Reached, Trigger Memory Resource Recommend.");
            R<?> recommended = resourceAllocationService.recommendMemoryResource(vmwareInfo.getUuid(), true);
            if (recommended.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Recommend Memory Resource Fail.");
            }
        }
    }

    private void processMemoryLow(VmwareInfo vmwareInfo, VmwareBasicThreshold vmwareBasicThreshold, VmwareMemoryThreshold vmwareMemoryThreshold) {
        // 1.记录当前时间戳
        long currentTimestamp = DateUtil.current();
        // 2.缓存当前 MEM 低位触发记录
        redisTemplate.opsForZSet().add(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.MEMORY_LOW + vmwareInfo.getUuid(), currentTimestamp, currentTimestamp);
        // 3.清除监测时间范围外的历史记录
        redisTemplate.opsForZSet().removeRangeByScore(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.MEMORY_LOW + vmwareInfo.getUuid(), 0, currentTimestamp - vmwareMemoryThreshold.getThresholdMemoryLowTimeRange() * 1000);
        // 4.获取监测时间范围内的低位触发数量
        Long count = redisTemplate.opsForZSet().size(VmwareRegulateConstant.LIVE_DATA_KEY_PREFIX + VmwareRegulateConstant.MEMORY_LOW + vmwareInfo.getUuid());
        if (null == count) {
            throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Get Memory Low Trigger Count Fail.");
        }
        // 5.计算时间范围内低位触发率
        double currentRate = 1.0 * count * vmwareBasicThreshold.getBasicDataMonitorPeriod() / vmwareMemoryThreshold.getThresholdMemoryLowTimeRange();
        log.info("Memory Low Trigger Count: {}, Current Rate: {}", count, currentRate);
        // 6.判断时间范围内低位触发率是否满足触发条件
        if (currentRate >= vmwareMemoryThreshold.getThresholdMemoryLowTimeRangeRate()) {
            log.info("Memory Low Trigger Rate Threshold is Not Reached.");
            return;
        }
        // 7.触发「MEM资源扩容」或「MEM资源推荐」
        if (VmwareRegulateStrategyEnum.AUTOMATIC.name().equals(vmwareBasicThreshold.getBasicRegulateStrategyType())) {
            log.info("Memory Low Trigger Rate Threshold is Reached, Trigger Memory Resource Expand.");
            R<?> expanded = resourceAllocationService.expandMemoryResource(vmwareInfo.getUuid(), true);
            if (expanded.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Expand Memory Resource Fail.");
            }
        } else {
            log.info("Memory Low Trigger Rate Threshold is Reached, Trigger Memory Resource Recommend.");
            R<?> recommended = resourceAllocationService.recommendMemoryResource(vmwareInfo.getUuid(), false);
            if (recommended.checkFail()) {
                throw new CommonException(BizCodeEnum.INTERNAL_MESSAGE.getCode(), "Recommend Memory Resource Fail.");
            }
        }
    }

    private CompletableFuture<Void> handlerSingle(VmwareInfo vmwareInfo) {
        return CompletableFuture.runAsync(() -> {
            // 1.查询虚拟机所有配置信息
            Map<String, String> configMap = dynamicConfigInfoMapper.selectList(Wrappers.<DynamicConfigInfo>lambdaQuery()
                    .eq(DynamicConfigInfo::getAffiliationMachineId, vmwareInfo.getId())
                    .eq(DynamicConfigInfo::getAffiliationType, DynamicConfigAffiliationTypeEnum.VMWARE)
            ).stream().collect(Collectors.toMap(DynamicConfigInfo::getConfigKey, DynamicConfigInfo::getConfigValue));

            // 2.解析虚拟机配置信息
            // 虚拟机数据监控周期
            Integer basicDataMonitorPeriod = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_BASIC_DATA_MONITOR_PERIOD.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_BASIC_DATA_MONITOR_PERIOD.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_BASIC_DATA_MONITOR_PERIOD.getValue();

            // 虚拟机调控最小时间间隔
            Integer basicRegulateMinTimeInterval = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_MIN_TIME_INTERVAL.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_MIN_TIME_INTERVAL.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_MIN_TIME_INTERVAL.getValue();

            // 虚拟机推荐消息最小时间间隔
            Integer basicRegulateRecommendMessageMinTimeInterval = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_RECOMMEND_MESSAGE_MIN_TIME_INTERVAL.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_RECOMMEND_MESSAGE_MIN_TIME_INTERVAL.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_RECOMMEND_MESSAGE_MIN_TIME_INTERVAL.getValue();

            // 虚拟机资源优先级类型
            String basicResourcePriorityType = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_BASIC_RESOURCE_PRIORITY_TYPE.getKey())
                    ? configMap.get(VmwareInitializeDataEnum.VMWARE_BASIC_RESOURCE_PRIORITY_TYPE.getKey())
                    : (String) VmwareInitializeDataEnum.VMWARE_BASIC_RESOURCE_PRIORITY_TYPE.getValue();

            // 虚拟机调控策略类型
            String basicRegulateStrategyType = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_STRATEGY_TYPE.getKey())
                    ? configMap.get(VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_STRATEGY_TYPE.getKey())
                    : (String) VmwareInitializeDataEnum.VMWARE_BASIC_REGULATE_STRATEGY_TYPE.getValue();

            // 虚拟机 CPU 高使用率监测值上限
            Integer thresholdCpuHighValue = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_VALUE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_VALUE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_VALUE.getValue();

            // 虚拟机 CPU 高使用率监测时间范围
            Integer thresholdCpuHighTimeRange = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE.getValue();

            // 虚拟机 CPU 高使用率时间范围内占比触发阈值
            Double thresholdCpuHighTimeRangeRate = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE_RATE.getKey())
                    ? Double.parseDouble(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE_RATE.getKey()))
                    : (Double) VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_HIGH_TIME_RANGE_RATE.getValue();

            // 虚拟机 CPU 低使用率监测值下限
            Integer thresholdCpuLowValue = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_VALUE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_VALUE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_VALUE.getValue();

            // 虚拟机 CPU 低使用率监测时间范围
            Integer thresholdCpuLowTimeRange = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE.getValue();

            // 虚拟机 CPU 低使用率时间范围内占比触发阈值
            Double thresholdCpuLowTimeRangeRate = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE_RATE.getKey())
                    ? Double.parseDouble(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE_RATE.getKey()))
                    : (Double) VmwareInitializeDataEnum.VMWARE_THRESHOLD_CPU_LOW_TIME_RANGE_RATE.getValue();

            // 虚拟机 MEM 高使用率监测值上限
            Integer thresholdMemoryHighValue = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_VALUE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_VALUE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_VALUE.getValue();

            // 虚拟机 MEM 高使用率监测时间范围
            Integer thresholdMemoryHighTimeRange = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE.getValue();

            // 虚拟机 MEM 高使用率时间范围内占比触发阈值
            Double thresholdMemoryHighTimeRangeRate = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE_RATE.getKey())
                    ? Double.parseDouble(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE_RATE.getKey()))
                    : (Double) VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_HIGH_TIME_RANGE_RATE.getValue();

            // 虚拟机 MEM 低使用率监测值下限
            Integer thresholdMemoryLowValue = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_VALUE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_VALUE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_VALUE.getValue();

            // 虚拟机 MEM 低使用率监测时间范围
            Integer thresholdMemoryLowTimeRange = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE.getKey())
                    ? Integer.parseInt(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE.getKey()))
                    : (Integer) VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE.getValue();

            // 虚拟机 MEM 低使用率时间范围内占比触发阈值
            Double thresholdMemoryLowTimeRangeRate = configMap.containsKey(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE_RATE.getKey())
                    ? Double.parseDouble(configMap.get(VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE_RATE.getKey()))
                    : (Double) VmwareInitializeDataEnum.VMWARE_THRESHOLD_MEMORY_LOW_TIME_RANGE_RATE.getValue();

            VmwareBasicThreshold vmwareBasicThreshold = new VmwareBasicThreshold();
            VmwareCpuThreshold vmwareCpuThreshold = new VmwareCpuThreshold();
            VmwareMemoryThreshold vmwareMemoryThreshold = new VmwareMemoryThreshold();
            vmwareBasicThreshold.setBasicDataMonitorPeriod(basicDataMonitorPeriod);
            vmwareBasicThreshold.setBasicRegulateMinTimeInterval(basicRegulateMinTimeInterval);
            vmwareBasicThreshold.setBasicRegulateRecommendMessageMinTimeInterval(basicRegulateRecommendMessageMinTimeInterval);
            vmwareBasicThreshold.setBasicResourcePriorityType(basicResourcePriorityType);
            vmwareBasicThreshold.setBasicRegulateStrategyType(basicRegulateStrategyType);
            vmwareCpuThreshold.setThresholdCpuHighValue(thresholdCpuHighValue);
            vmwareCpuThreshold.setThresholdCpuHighTimeRange(thresholdCpuHighTimeRange);
            vmwareCpuThreshold.setThresholdCpuHighTimeRangeRate(thresholdCpuHighTimeRangeRate);
            vmwareCpuThreshold.setThresholdCpuLowValue(thresholdCpuLowValue);
            vmwareCpuThreshold.setThresholdCpuLowTimeRange(thresholdCpuLowTimeRange);
            vmwareCpuThreshold.setThresholdCpuLowTimeRangeRate(thresholdCpuLowTimeRangeRate);
            vmwareMemoryThreshold.setThresholdMemoryHighValue(thresholdMemoryHighValue);
            vmwareMemoryThreshold.setThresholdMemoryHighTimeRange(thresholdMemoryHighTimeRange);
            vmwareMemoryThreshold.setThresholdMemoryHighTimeRangeRate(thresholdMemoryHighTimeRangeRate);
            vmwareMemoryThreshold.setThresholdMemoryLowValue(thresholdMemoryLowValue);
            vmwareMemoryThreshold.setThresholdMemoryLowTimeRange(thresholdMemoryLowTimeRange);
            vmwareMemoryThreshold.setThresholdMemoryLowTimeRangeRate(thresholdMemoryLowTimeRangeRate);

            // 3. 处理虚拟机 CPU、MEMORY
            try {
                processCpu(vmwareInfo, vmwareBasicThreshold, vmwareCpuThreshold);
                processMemory(vmwareInfo, vmwareBasicThreshold, vmwareMemoryThreshold);
            } catch (CommonException e) {
                log.error("VmwareUuid:{}, Regulate Fail. Error:{}", vmwareInfo.getUuid(), e.getMessage());
                log.error(e.toString());
            }
        });
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            log.info("[虚拟机-CPU-MEMORY-调控任务] 线程启动！^START^ ");

            // 1.查询正在运行的虚拟机
            List<VmwareInfo> vmwareInfos = vmwareInfoMapper.selectList(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getState, VmwareStateEnum.RUNNING));

            // 2.遍历处理所有虚拟机
            if (vmwareInfos != null && !vmwareInfos.isEmpty()) {
                List<CompletableFuture<Void>> handlerFutureList = vmwareInfos.stream().map(this::handlerSingle).collect(Collectors.toList());
                CompletableFuture<Void>[] handlerFutureArray = handlerFutureList.toArray(new CompletableFuture[handlerFutureList.size()]);
                CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(handlerFutureArray);
                allOfFuture.join();
            }

            log.info("[虚拟机-CPU-MEMORY-调控任务] 执行完成！^ END ^");
        } catch (Exception e) {
            log.error("当前周期内数据监控「调控」任务异常退出，请检查报错信息！错误信息：{}", e.getMessage());
            log.error(e.toString());
        }
    }
}
