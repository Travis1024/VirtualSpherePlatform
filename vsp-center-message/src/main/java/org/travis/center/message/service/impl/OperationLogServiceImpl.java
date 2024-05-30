package org.travis.center.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.message.OperationLog;
import org.travis.center.common.mapper.message.OperationLogMapper;
import org.travis.center.message.service.OperationLogService;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

/**
 * @ClassName OperationLogServiceImpl
 * @Description OperationLogServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService{

    @Override
    public PageResult<OperationLog> pageSelectList(PageQuery pageQuery) {
        Page<OperationLog> operationLogPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(operationLogPage);
    }
}
