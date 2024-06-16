package org.travis.center.web.crontab;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.entity.support.CrontabInfo;
import org.travis.center.common.entity.support.OperationLog;
import org.travis.center.common.enums.HostStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.mapper.support.CrontabInfoMapper;
import org.travis.center.common.mapper.table.TableMapper;
import org.travis.center.support.service.OperationLogService;
import org.travis.shared.common.constants.CrontabConstant;
import org.travis.shared.common.constants.DatabaseConstant;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.utils.TableMonthThreadLocalUtil;
import org.travis.shared.common.utils.TimeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @ClassName CrontabConfigService
 * @Description CrontabConfigService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Slf4j
@Component
public class CrontabScheduleService implements SchedulingConfigurer {

    @Resource
    public RedissonClient redissonClient;
    @Resource
    public CrontabInfoMapper crontabInfoMapper;
    @Resource
    public OperationLogService operationLogService;
    @Resource
    public TableMapper tableMapper;
    @Value("${vsp.database.schema}")
    public String schema;
    @Resource
    public Cache<String, Object> commonPermanentCache;
    @Resource
    public HostInfoMapper hostInfoMapper;
    @Resource
    public VmwareInfoMapper vmwareInfoMapper;

    private static final Long millisecond_30s = 30 * 1000L;


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 在注册器添加定时任务前添加线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(4));

        // 1.日志数据定时持久化定时任务
        taskRegistrar.addTriggerTask(
                // 1.获取所有缓存操作日志列表
                this::operateLogHandleMethod,
                // 2.设置任务执行周期
                triggerContext -> {
                    // 2.1.从缓存或数据库中获取执行周期
                    String cronExpression = queryCronExpression(CrontabConstant.LOG_TASK_INDEX_ID);
                    // 2.2.cron 合法性校验
                    if (StrUtil.isBlank(cronExpression)) {
                        log.warn("[Crontab-Operation-Log] No related expression is found, the default expression is used!");
                        cronExpression = CrontabConstant.CRON_30_S;
                    }
                    // 2.3.返回执行周期
                    return new CronTrigger(cronExpression).nextExecutionTime(triggerContext);
                }
        );

        // 2.日志数据月份表定时创建任务
        taskRegistrar.addTriggerTask(
                // 1.表创建任务
                this::logTableCreateHandleMethod,
                // 2.设置任务执行周期
                triggerContext -> {
                    // 2.1.从缓存或数据库中获取执行周期
                    String cronExpression = queryCronExpression(CrontabConstant.LOG_TABLE_CREATE_INDEX_ID);
                    // 2.2.cron 合法性校验
                    if (StrUtil.isBlank(cronExpression)) {
                        log.warn("[Crontab-Log-Table-Create] No related expression is found, the default expression is used!");
                        cronExpression = CrontabConstant.CRON_26_27_28_PER_MONTH;
                    }
                    // 2.3.返回执行周期
                    return new CronTrigger(cronExpression).nextExecutionTime(triggerContext);
                }
        );

        // 3.宿主机虚拟机状态定时更新任务
        taskRegistrar.addTriggerTask(
                // 1.状态更新任务
                this::operateMachineStateUpdateHandle,
                // 2.设置任务执行周期
                triggerContext -> {
                    // 2.1.从缓存或数据库中获取执行周期
                    String cronExpression = queryCronExpression(CrontabConstant.MACHINE_STATE_UPDATE_INDEX_ID);
                    // 2.2.cron 合法性校验
                    if (StrUtil.isBlank(cronExpression)) {
                        log.warn("[Crontab-Machine-State-Update] No related expression is found, the default expression is used!");
                        cronExpression = CrontabConstant.CRON_30_S;
                    }
                    // 2.3.返回执行周期
                    return new CronTrigger(cronExpression).nextExecutionTime(triggerContext);
                }
        );
    }

    private void operateMachineStateUpdateHandle() {
        // 1.程序启动预热期判断
        Long startTime = ((Long) commonPermanentCache.getIfPresent(SystemConstant.PROGRAM_START_TIME_KEY));
        if (ObjectUtil.isNull(startTime)) {
            log.error("[CrontabScheduleService::operateMachineStateUpdateHandle] No start time is found!");
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - startTime <= 60000) {
            log.warn("[[CrontabScheduleService::operateMachineStateUpdateHandle] Program warming up···");
            return;
        }

        // 2.宿主机状态判断及修改
        List<HostInfo> hostInfoList = hostInfoMapper.selectList(Wrappers.<HostInfo>lambdaQuery().select(HostInfo::getId, HostInfo::getIp, HostInfo::getState));
        for (HostInfo hostInfo : hostInfoList) {
            RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(RedissonConstant.HEALTHY_HOST_RECORDS + hostInfo.getIp());

            // 2.1.判断是否健康
            Collection<String> valuedRange = sortedSet.valueRange(currentTimeMillis - millisecond_30s, true, currentTimeMillis, true);
            boolean isHealthy = !valuedRange.isEmpty();
            // 删除历史数据
            sortedSet.removeRangeByScore(0, true, currentTimeMillis - millisecond_30s, true);

            // 2.1.「准备中」or「停用」or「初始化异常」状态忽略，不进行处理
            if (HostStateEnum.IN_PREPARATION.equals(hostInfo.getState()) || HostStateEnum.DISABLE.equals(hostInfo.getState()) || HostStateEnum.INIT_ERROR.equals(hostInfo.getState())) {
                continue;
            }

            // 2.2.「就绪」状态 + 无心跳，修改状态为「心跳异常」
            if (!isHealthy && HostStateEnum.READY.equals(hostInfo.getState())) {
                hostInfoMapper.update(Wrappers.<HostInfo>lambdaUpdate().set(HostInfo::getState, HostStateEnum.HEART_BEAT_ERROR).eq(HostInfo::getId, hostInfo.getId()));
            }

            // 2.3.「心跳异常」状态 + 有心跳，修改状态为「就绪」
            if (isHealthy && HostStateEnum.HEART_BEAT_ERROR.equals(hostInfo.getState())) {
                hostInfoMapper.update(Wrappers.<HostInfo>lambdaUpdate().set(HostInfo::getState, HostStateEnum.READY).eq(HostInfo::getId, hostInfo.getId()));
            }
        }

        // TODO 3.虚拟机状态判断及修改
        List<VmwareInfo> vmwareInfoList = vmwareInfoMapper.selectList(Wrappers.<VmwareInfo>lambdaQuery().select(VmwareInfo::getId, VmwareInfo::getUuid, VmwareInfo::getState));
        RMap<String, String> rMap = redissonClient.getMap(RedissonConstant.HEALTHY_VMWARE_RECORDS);

    }

    private void operateLogHandleMethod() {
        try {
            log.info("[Crontab-Task-Start] Operation Log persistence crontab schedule started");
            RBlockingDeque<OperationLog> blockingDeque = redissonClient.getBlockingDeque(RedissonConstant.LOG_CACHE_DATA_KEY);
            List<OperationLog> operationLogs = new ArrayList<>();
            blockingDeque.drainTo(operationLogs);
            // 如果缓存中有数据，进行持久化
            if (!operationLogs.isEmpty()) {
                TableMonthThreadLocalUtil.setData(TimeUtil.getCurrentYearMonth());
                operationLogService.saveBatch(operationLogs);
            }
            log.info("[Crontab-Task-Finish] Operation Log Count -> {}", operationLogs.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            TableMonthThreadLocalUtil.removeData();
        }
    }

    private void logTableCreateHandleMethod() {
        log.info("[Crontab-Task-Start] Log Table Create started");
        String tableName = DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX + StrUtil.UNDERLINE + TimeUtil.getNextYearMonth();
        String schemaTableName = schema + StrUtil.DOT + tableName;
        tableMapper.createOperationLogTable(tableName, schemaTableName);
        log.info("[Crontab-Task-Finish] Log Table Create -> {}", schemaTableName);
    }

    /**
     * @MethodName queryCronExpression
     * @Description 根据定时任务 ID 查询 Crontab 表达式
     * @Author travis-wei
     * @Data 2024/5/31
     * @param crontabInfoId	定时任务ID
     * @Return java.lang.String
     **/
    private String queryCronExpression(Long crontabInfoId) {
        RMap<Long, CrontabInfo> rMap = redissonClient.getMap(RedissonConstant.CRONTAB_CACHE_KEY);
        CrontabInfo crontabInfo = rMap.get(crontabInfoId);
        if (ObjectUtil.isNull(crontabInfo)) {
            crontabInfo = crontabInfoMapper.selectById(crontabInfoId);
            if (ObjectUtil.isNotEmpty(crontabInfo)) {
                rMap.put(crontabInfoId, crontabInfo);
            }
        }
        return ObjectUtil.isNull(crontabInfo) || StrUtil.isBlank(crontabInfo.getCronExpression()) ? null : crontabInfo.getCronExpression();
    }
}
