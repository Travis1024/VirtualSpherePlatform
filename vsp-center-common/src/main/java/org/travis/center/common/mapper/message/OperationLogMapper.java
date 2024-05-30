package org.travis.center.common.mapper.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.message.OperationLog;

/**
 * @ClassName OperationLogMapper
 * @Description OperationLogMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
