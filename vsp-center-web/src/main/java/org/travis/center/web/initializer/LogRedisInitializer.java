package org.travis.center.web.initializer;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.message.CrontabInfo;
import org.travis.center.common.mapper.message.CrontabInfoMapper;
import org.travis.shared.common.constants.RedissonConstant;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName CenterRedisInitializer
 * @Description CenterRedisInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Slf4j
@Component
@Order(1)
public class LogRedisInitializer implements CommandLineRunner {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private CrontabInfoMapper crontabInfoMapper;

    @Override
    public void run(String... args) {
        // 查询所有定时任务 ID 列表
        List<Long> cronIds = LogDatabaseInitializer.CRONTAB_INFOS.stream().map(CrontabInfo::getId).collect(Collectors.toList());
        // 根据 ID 列表查询定时任务
        List<CrontabInfo> crontabInfoList = crontabInfoMapper.selectBatchIds(cronIds);
        // 缓存到 redis
        Map<Long, CrontabInfo> crontabInfoMap = crontabInfoList.stream().collect(Collectors.toMap(CrontabInfo::getId, crontabInfo -> crontabInfo));
        RMap<Object, Object> rMap = redissonClient.getMap(RedissonConstant.CRONTAB_CACHE_KEY);
        rMap.putAll(crontabInfoMap);
    }
}
