package org.travis.center.web.initializer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.message.CrontabInfo;
import org.travis.center.common.mapper.message.CrontabInfoMapper;
import org.travis.shared.common.constants.CrontabConstant;
import org.travis.shared.common.utils.CrontabUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName CenterDatabaseInitializer
 * @Description CenterDatabaseInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Slf4j
@Component
@Order(0)
public class LogDatabaseInitializer implements CommandLineRunner {

    @Resource
    private CrontabInfoMapper crontabInfoMapper;

    public static final List<CrontabInfo> CRONTAB_INFOS = new ArrayList<>();

    static {
        CrontabInfo logCrontabInfo = CrontabInfo.builder()
                .id(CrontabConstant.LOG_TASK_INDEX_ID)
                .cronName("日志持久化任务")
                .cronExpression(CrontabConstant.CRON_30_S)
                .cronDescription(StrUtil.format(CrontabConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(CrontabConstant.CRON_30_S)))
                .build();

        CRONTAB_INFOS.add(logCrontabInfo);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database...");
        Set<Long> initIds = CRONTAB_INFOS.stream().map(CrontabInfo::getId).collect(Collectors.toSet());
        Set<Long> existIds = crontabInfoMapper.selectBatchIds(initIds).stream().map(CrontabInfo::getId).collect(Collectors.toSet());
        initIds.removeAll(existIds);
        log.info(JSONUtil.toJsonStr(initIds));
        CRONTAB_INFOS.forEach(crontabInfo -> {
           if (initIds.contains(crontabInfo.getId())) {
               crontabInfoMapper.insert(crontabInfo);
           }
        });
        log.info("Initializing database completed.");
    }
}
