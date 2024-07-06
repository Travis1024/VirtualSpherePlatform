package org.travis.center.web.initializer;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigTypeEnum;
import org.travis.center.common.mapper.support.DynamicConfigInfoMapper;
import org.travis.center.support.processor.MonitorPeriodDynamicConfigService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MonitorPeriodCacheInitializer
 * @Description MonitorPeriodCacheInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/3
 */
@Slf4j
@Component
@Order(5)
public class MonitorPeriodCacheInitializer implements CommandLineRunner {

    @Resource
    public DynamicConfigInfoMapper dynamicConfigInfoMapper;
    @Resource
    public MonitorPeriodDynamicConfigService monitorPeriodDynamicConfigService;

    @Override
    public void run(String... args) throws Exception {
        log.info("[5] MonitorPeriodCacheInitializer start");
        List<DynamicConfigInfo> dynamicConfigInfoList = dynamicConfigInfoMapper.selectList(
                Wrappers.<DynamicConfigInfo>lambdaQuery().eq(DynamicConfigInfo::getConfigType, DynamicConfigTypeEnum.MONITOR_PERIOD)
        );
        // 初始化：向 redis 中添加缓存
        dynamicConfigInfoList.forEach(dynamicConfigInfo -> monitorPeriodDynamicConfigService.addMonitorPeriodCache(dynamicConfigInfo));
    }
}
