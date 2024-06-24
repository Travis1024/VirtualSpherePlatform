package org.travis.center.web.jobs;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.travis.center.common.mapper.table.TableMapper;
import org.travis.shared.common.constants.DatabaseConstant;
import org.travis.shared.common.utils.TimeUtil;

import javax.annotation.Resource;

/**
 * @ClassName QuartzLogTableCreateJob
 * @Description 日志数据月份表定时创建任务
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/25
 */
@Slf4j
@Component
public class QuartzLogTableCreateJob extends QuartzJobBean {

    @Value("${vsp.database.schema}")
    public String schema;
    @Resource
    public TableMapper tableMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logTableCreateHandleMethod();
    }

    private void logTableCreateHandleMethod() {
        log.info("[Crontab-Task-Start] Log Table Create started");
        String tableName = DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX + StrUtil.UNDERLINE + TimeUtil.getNextYearMonth();
        String schemaTableName = schema + StrUtil.DOT + tableName;
        tableMapper.createOperationLogTable(tableName, schemaTableName);
        log.info("[Crontab-Task-Finish] Log Table Create -> {}", schemaTableName);
    }
}
