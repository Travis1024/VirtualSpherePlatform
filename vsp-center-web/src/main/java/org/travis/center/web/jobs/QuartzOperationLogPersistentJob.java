package org.travis.center.web.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.OperationLog;
import org.travis.center.support.service.OperationLogService;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.utils.TableMonthThreadLocalUtil;
import org.travis.shared.common.utils.TimeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName QuartzOperationLogPersistentJob
 * @Description 操作日志定时持久化定时任务
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/24
 */
@Slf4j
@Component
public class QuartzOperationLogPersistentJob extends QuartzJobBean {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private OperationLogService operationLogService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        operateLogHandleMethod();
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
}
