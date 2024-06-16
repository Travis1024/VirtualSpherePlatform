package org.travis.center.web.initializer;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.support.CrontabInfo;
import org.travis.center.common.mapper.support.CrontabInfoMapper;
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
@Order(3)
public class CrontabInfoRedisInitializer implements CommandLineRunner {

    @Resource
    public RedissonClient redissonClient;
    @Resource
    public CrontabInfoMapper crontabInfoMapper;

    @Override
    public void run(String... args) {
        log.info("[3] Initializing CrontabInfo Redis Cache Data");
        // 查询所有定时任务 ID 列表
        List<Long> cronIds = CrontabInfoDatabaseInitializer.CRONTAB_INFOS.stream().map(CrontabInfo::getId).collect(Collectors.toList());
        // 根据 ID 列表查询定时任务
        List<CrontabInfo> crontabInfoList = crontabInfoMapper.selectBatchIds(cronIds);
        // 缓存到 redis
        Map<Long, CrontabInfo> crontabInfoMap = crontabInfoList.stream().collect(Collectors.toMap(CrontabInfo::getId, crontabInfo -> crontabInfo));
        RMap<Long, CrontabInfo> rMap = redissonClient.getMap(RedissonConstant.CRONTAB_CACHE_KEY);
        rMap.putAll(crontabInfoMap);
        log.info("[3] Initializing CrontabInfo Redis Cache Data Completed.");
    }
}
