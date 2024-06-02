package org.travis.center.web.initializer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.CrontabInfo;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigTypeEnum;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @ClassName DynamicConfigDatabaseInitializer
 * @Description DynamicConfigDatabaseInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Slf4j
@Component
@Order(4)
public class DynamicConfigDatabaseInitializer implements CommandLineRunner {

    @Resource
    private DynamicConfigInfoMapper dynamicConfigInfoMapper;

    private static final AtomicLong INDEX_START_NUMBER = new AtomicLong(1L);

    public static final List<DynamicConfigInfo> DYNAMIC_CONFIG_INFOS = new ArrayList<>();

    static {
        DynamicConfigInfo monitoringPeriodConfig = DynamicConfigInfo.builder()
                .id(INDEX_START_NUMBER.getAndIncrement())
                .configName("监控周期(s)")
                .configDescription("集群内主机指标监控周期-单位秒")
                .configKey("monitor.period")
                .configValue("5")
                .configExample("5")
                .configType(DynamicConfigTypeEnum.MONITOR)
                .build();

        DYNAMIC_CONFIG_INFOS.add(monitoringPeriodConfig);
    }

    @Override
    public void run(String... args) {
        log.info("[4] Initializing Dynamic Config Database");
        Set<Long> initIds = DYNAMIC_CONFIG_INFOS.stream().map(DynamicConfigInfo::getId).collect(Collectors.toSet());
        Set<Long> existIds = dynamicConfigInfoMapper.selectBatchIds(initIds).stream().map(DynamicConfigInfo::getId).collect(Collectors.toSet());
        initIds.removeAll(existIds);
        log.info(JSONUtil.toJsonStr(initIds));
        DYNAMIC_CONFIG_INFOS.forEach(dynamicConfigInfo -> {
            if (initIds.contains(dynamicConfigInfo.getId())) {
                dynamicConfigInfoMapper.insert(dynamicConfigInfo);
            }
        });
        log.info("[4] Initializing Dynamic Config Database Completed.");
    }
}
