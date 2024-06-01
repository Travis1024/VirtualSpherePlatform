package org.travis.center.web.initializer;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.mapper.table.TableMapper;
import org.travis.shared.common.constants.DatabaseConstant;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.utils.TimeUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName LogTableDatabaseInitializer
 * @Description LogTableDatabaseInitializer
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@Slf4j
@Component
@Order(1)
public class LogTableDatabaseInitializer implements CommandLineRunner {

    @Value("${vsp.database.schema}")
    private String schema;
    @Resource
    private TableMapper tableMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(String... args) throws Exception {
        log.info("[1.1]「OperationLog」Table Initializing");
        String tableName = DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX + StrUtil.UNDERLINE + TimeUtil.getCurrentYearMonth();
        String schemaTableName = schema + StrUtil.DOT + tableName;
        tableMapper.createOperationLogTable(tableName, schemaTableName);
        log.info("[1.1]「OperationLog」Table created.");

        log.info("[1.2]「OperationLog」Table Time Range Redis Cache Initializing");
        List<String> stringList = tableMapper.checkTableExistence(StrUtil.format("{}_%", DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX));
        List<String> yyyyMmList = stringList.stream().map(one -> one.substring(one.lastIndexOf(StrUtil.UNDERLINE) + 1)).collect(Collectors.toList());
        redissonClient.getSet(RedissonConstant.DYNAMIC_TABLE_TIMES_PREFIX + DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX).addAll(yyyyMmList);
        log.info("[1.2]「OperationLog」Table Time Range Redis Cache Initializing");
    }
}
