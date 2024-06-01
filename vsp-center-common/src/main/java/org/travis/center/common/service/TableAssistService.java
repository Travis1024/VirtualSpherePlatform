package org.travis.center.common.service;

import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.travis.center.common.mapper.table.TableMapper;
import org.travis.shared.common.constants.RedissonConstant;

import javax.annotation.Resource;

/**
 * @ClassName TableAssistService
 * @Description TableAssistService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@Service
public class TableAssistService {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 校验月份表是否存在
     *
     * @param tableName 原表名称
     * @param yyyyMm    月份
     * @return  true-存在
     */
    public boolean checkDynamicTable(String tableName, String yyyyMm) {
        RSet<String> rSet = redissonClient.getSet(RedissonConstant.DYNAMIC_TABLE_TIMES_PREFIX + tableName);
        return rSet != null && rSet.contains(yyyyMm);
    }

    /**
     * 新增月份表缓存
     *
     * @param tableName 原表名称
     * @param yyyyMm    月份
     */
    public void insertDynamicTableCache(String tableName, String yyyyMm) {
        redissonClient.getSet(RedissonConstant.DYNAMIC_TABLE_TIMES_PREFIX + tableName).add(yyyyMm);
    }
}
