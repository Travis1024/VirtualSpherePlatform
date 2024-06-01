package org.travis.center.message.service;

import org.travis.center.common.entity.message.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.message.pojo.dto.OperationLogQueryDTO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

/**
 * @ClassName OperationLogService
 * @Description OperationLogService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
public interface OperationLogService extends IService<OperationLog>{
    PageResult<OperationLog> pageSelectList(OperationLogQueryDTO operationLogQueryDTO);
}
