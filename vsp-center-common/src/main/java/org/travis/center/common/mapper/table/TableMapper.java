package org.travis.center.common.mapper.table;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @ClassName TableMapper
 * @Description TableMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
@Mapper
public interface TableMapper {

    @Select("SELECT COUNT(*)  from dba_tables WHERE TABLE_NAME = #{tableName}")
    int checkTableExistence(String tableName);

    void createOperationLogTable(@Param("tableName") String tableName, @Param("schemaTableName") String schemaTableName);
}
