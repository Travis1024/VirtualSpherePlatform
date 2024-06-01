package org.travis.center.common.mapper.table;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @ClassName TableMapper
 * @Description TableMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@Mapper
public interface TableMapper {

    List<String> checkTableExistence(@Param("tableName") String tableName);

    void createOperationLogTable(@Param("tableName") String tableName, @Param("schemaTableName") String schemaTableName);
}
