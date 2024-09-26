package org.travis.center.web.initializer;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName DynamicConfigDatabaseInitializer
 * @Description 动态参数数据库初始化器
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/2
 */
@Slf4j
@Component
@Order(3)
public class DynamicConfigDatabaseInitializer implements CommandLineRunner {

    @Resource
    public DynamicConfigInfoMapper dynamicConfigInfoMapper;

    public static final List<DynamicConfigInfo> DYNAMIC_CONFIG_INFOS = new ArrayList<>();

    static {
        // TODO 添加待持久化的动态参数配置
        // DynamicConfigInfo monitoringPeriodConfig = DynamicConfigInfo.builder()
        //         .id()
        //         .configName("")
        //         .configDescription("")
        //         .configValue("")
        //         .configExample("")
        //         .configType(DynamicConfigTypeEnum.)
        //         .build();
        //
        // DYNAMIC_CONFIG_INFOS.add(monitoringPeriodConfig);
    }

    @Override
    public void run(String... args) {
        log.info("[3] Initializing Dynamic Config Database");
        Set<Long> initIds = DYNAMIC_CONFIG_INFOS.stream().map(DynamicConfigInfo::getId).collect(Collectors.toSet());

        if (!initIds.isEmpty()) {
            Set<Long> existIds = dynamicConfigInfoMapper.selectBatchIds(initIds).stream().map(DynamicConfigInfo::getId).collect(Collectors.toSet());
            initIds.removeAll(existIds);
            log.info(JSONUtil.toJsonStr(initIds));
            DYNAMIC_CONFIG_INFOS.forEach(dynamicConfigInfo -> {
                if (initIds.contains(dynamicConfigInfo.getId())) {
                    dynamicConfigInfoMapper.insert(dynamicConfigInfo);
                }
            });
        }

        log.info("[3] Initializing Dynamic Config Database Completed.");
    }
}
