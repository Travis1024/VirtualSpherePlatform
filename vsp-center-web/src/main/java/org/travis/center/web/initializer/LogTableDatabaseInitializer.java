package org.travis.center.web.initializer;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.travis.center.common.mapper.table.TableMapper;
import org.travis.shared.common.constants.DatabaseConstant;
import org.travis.shared.common.utils.TimeUtil;

import javax.annotation.Resource;

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

    @Override
    public void run(String... args) throws Exception {
        log.info("[1]「OperationLog」Table Initializing");
        String tableName = DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX + StrUtil.UNDERLINE + TimeUtil.getCurrentYearMonth();
        String schemaTableName = schema + StrUtil.DOT + tableName;
        tableMapper.createOperationLogTable(tableName, schemaTableName);
        log.info("[1]「OperationLog」Table created.");

    }
}
