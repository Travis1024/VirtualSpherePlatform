package org.travis.center.support.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.support.OperationLog;
import org.travis.center.common.mapper.support.OperationLogMapper;
import org.travis.center.common.service.TableAssistService;
import org.travis.center.support.pojo.dto.OperationLogQueryDTO;
import org.travis.center.support.service.OperationLogService;
import org.travis.shared.common.constants.DatabaseConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.NotFoundException;
import org.travis.shared.common.utils.TableMonthThreadLocalUtil;

import javax.annotation.Resource;

/**
 * @ClassName OperationLogServiceImpl
 * @Description OperationLogServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService{
    @Resource
    private TableAssistService tableAssistService;

    @Override
    public PageResult<OperationLog> pageSelectList(OperationLogQueryDTO operationLogQueryDTO) {
        PageQuery pageQuery = operationLogQueryDTO.getPageQuery();
        Page<OperationLog> operationLogPage;
        // 判断当前月份数据表是否存在
        Assert.isTrue(tableAssistService.checkDynamicTable(DatabaseConstant.OPERATION_LOG_TABLE_NAME_PREFIX, operationLogQueryDTO.getYyyyMm()), () -> new NotFoundException("此月份数据表不存在!"));
        try {
            TableMonthThreadLocalUtil.setData(operationLogQueryDTO.getYyyyMm());
            operationLogPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        } finally {
            TableMonthThreadLocalUtil.removeData();
        }
        return PageResult.of(operationLogPage);
    }
}
