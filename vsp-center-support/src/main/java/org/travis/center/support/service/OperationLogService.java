package org.travis.center.support.service;

import org.travis.center.common.entity.support.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.support.pojo.dto.OperationLogQueryDTO;
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
