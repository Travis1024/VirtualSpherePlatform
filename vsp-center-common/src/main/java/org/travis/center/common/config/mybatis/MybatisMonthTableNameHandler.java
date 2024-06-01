package org.travis.center.common.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import org.travis.shared.common.utils.TableMonthThreadLocalUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MybatisMonthTableNameHandler
 * @Description MybatisMonthTableNameHandler
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
public class MybatisMonthTableNameHandler implements TableNameHandler {

    // 用于记录哪些表可以使用该月份动态表名处理器（即哪些表按月分表）
    private static List<String> tableNames = null;

    // 构造函数，构造动态表名处理器的时候，传递tableNames参数
    public MybatisMonthTableNameHandler(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    @Override
    public String dynamicTableName(String sql, String tableName) {
        if (this.tableNames.contains(tableName)) {
            // 表名增加月份后缀
            return tableName + "_" + TableMonthThreadLocalUtil.getData();
        } else {
            // 表名原样返回
            return tableName;
        }
    }
}
