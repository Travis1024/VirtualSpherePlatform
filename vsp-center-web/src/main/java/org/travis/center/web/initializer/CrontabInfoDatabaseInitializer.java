package org.travis.center.web.initializer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.CrontabInfo;
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.mapper.support.CrontabInfoMapper;
import org.travis.shared.common.constants.CrontabConstant;
import org.travis.shared.common.utils.CrontabUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
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
@Order(2)
public class CrontabInfoDatabaseInitializer implements CommandLineRunner {

    @Resource
    public CrontabInfoMapper crontabInfoMapper;

    public static final List<CrontabInfo> CRONTAB_INFOS = new ArrayList<>();

    static {
        CrontabInfo logCrontabInfo = CrontabInfo.builder()
                .id(CrontabConstant.LOG_TASK_INDEX_ID)
                .cronName("日志持久化定时任务")
                .cronExpression(CrontabConstant.CRON_30_S)
                .cronDescription(StrUtil.format(CrontabConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(CrontabConstant.CRON_30_S)))
                .isFixed(IsFixedEnum.ALLOW_UPDATE)
                .build();

        CrontabInfo logTableCreateCrontabInfo = CrontabInfo.builder()
                .id(CrontabConstant.LOG_TABLE_CREATE_INDEX_ID)
                .cronName("操作日志月份数据表定时创建任务")
                .cronExpression(CrontabConstant.CRON_26_27_28_PER_MONTH)
                .cronDescription("执行周期：每月 26-28 号 2:00 各执行一次")
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .build();

        CrontabInfo machineStateUpdateCrontabInfo = CrontabInfo.builder()
                .id(CrontabConstant.MACHINE_STATE_UPDATE_INDEX_ID)
                .cronName("宿主机虚拟机状态定时更新任务")
                .cronExpression(CrontabConstant.CRON_30_S)
                .cronDescription(StrUtil.format(CrontabConstant.CRON_DESCRIPTION_TEMPLATE, CrontabUtil.getCrontabIntervalInSeconds(CrontabConstant.CRON_30_S)))
                .isFixed(IsFixedEnum.DISALLOW_UPDATE)
                .build();

        CRONTAB_INFOS.add(logCrontabInfo);
        CRONTAB_INFOS.add(logTableCreateCrontabInfo);
        CRONTAB_INFOS.add(machineStateUpdateCrontabInfo);
    }

    @Override
    public void run(String... args) {
        log.info("[2] Initializing CrontabInfo Records");
        Set<Long> initIds = CRONTAB_INFOS.stream().map(CrontabInfo::getId).collect(Collectors.toSet());
        Set<Long> existIds = crontabInfoMapper.selectBatchIds(initIds).stream().map(CrontabInfo::getId).collect(Collectors.toSet());
        initIds.removeAll(existIds);
        log.info(JSONUtil.toJsonStr(initIds));
        CRONTAB_INFOS.forEach(crontabInfo -> {
           if (initIds.contains(crontabInfo.getId())) {
               crontabInfoMapper.insert(crontabInfo);
           }
        });
        log.info("[2] Initializing CrontabInfo Records Completed.");
    }
}
