package org.travis.center.support.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.support.OperationLog;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.pojo.dto.OperationLogQueryDTO;
import org.travis.center.support.service.OperationLogService;
import org.springframework.web.bind.annotation.*;
import org.travis.shared.common.domain.PageResult;
import javax.annotation.Resource;

/**
* (VSP.VSP_OPERATION_LOG)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/operationLog")
public class OperationLogController {
    @Resource
    public OperationLogService operationLogService;

    @Log(title = "按月份分页查询操作日志列表信息", businessType = BusinessTypeEnum.QUERY, isSaveResponseData = false)
    @Operation(summary = "分页查询操作日志列表信息")
    @PostMapping("/pageSelect")
    public PageResult<OperationLog> pageSelectList(@Validated @RequestBody OperationLogQueryDTO operationLogQueryDTO) {
        return operationLogService.pageSelectList(operationLogQueryDTO);
    }
}
