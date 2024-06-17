package org.travis.center.support.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.support.CrontabInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.pojo.dto.CrontabUpdateDTO;
import org.travis.center.support.service.CrontabInfoService;
import org.springframework.web.bind.annotation.*;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* (VSP.VSP_CRONTAB_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/crontab")
public class CrontabInfoController {

    @Resource
    public CrontabInfoService crontabInfoService;

    @Log(title = "查询定时任务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询定时任务列表")
    @GetMapping("/select")
    public List<CrontabInfo> selectList() {
        return crontabInfoService.selectList();
    }

    @Log(title = "分页查询定时任务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询定时任务列表")
    @GetMapping("/pageSelect")
    public PageResult<CrontabInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return crontabInfoService.pageSelectList(pageQuery);
    }

    @Log(title = "更新定时任务Crontab表达式", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "更新定时任务Crontab表达式")
    @PutMapping("/updateCron")
    public void updateCronExpression(@Valid @RequestBody CrontabUpdateDTO crontabUpdateDTO) {
        crontabInfoService.updateCronExpression(crontabUpdateDTO);
    }

}
