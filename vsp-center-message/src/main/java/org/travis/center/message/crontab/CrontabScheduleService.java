package org.travis.center.message.crontab;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.message.CrontabInfo;
import org.travis.center.common.entity.message.OperationLog;
import org.travis.center.common.mapper.message.CrontabInfoMapper;
import org.travis.center.message.service.OperationLogService;
import org.travis.shared.common.constants.CrontabConstant;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.utils.TableMonthThreadLocalUtil;
import org.travis.shared.common.utils.TimeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private RedissonClient redissonClient;
    @Resource
    private CrontabInfoMapper crontabInfoMapper;
    @Resource
    private OperationLogService operationLogService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 在注册器添加定时任务前添加线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(4));

        // 日志数据定时持久化定时任务
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
        if (ObjectUtil.isEmpty(crontabInfo)) {
            crontabInfo = crontabInfoMapper.selectById(crontabInfoId);
            if (ObjectUtil.isNotEmpty(crontabInfo)) {
                rMap.put(crontabInfoId, crontabInfo);
            }
        }
        return ObjectUtil.isEmpty(crontabInfo) || StrUtil.isBlank(crontabInfo.getCronExpression()) ? null : crontabInfo.getCronExpression();
    }
}
