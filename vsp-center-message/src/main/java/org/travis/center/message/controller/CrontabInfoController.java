package org.travis.center.message.controller;
import io.swagger.v3.oas.annotations.Operation;
import org.travis.center.common.entity.message.CrontabInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.message.aspect.Log;
import org.travis.center.message.pojo.dto.CrontabUpdateDTO;
import org.travis.center.message.service.CrontabInfoService;
import org.travis.center.message.service.impl.CrontabInfoServiceImpl;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

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
    private CrontabInfoService crontabInfoService;

    @Log(title = "查询定时任务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询定时任务列表")
    @GetMapping("/select")
    public List<CrontabInfo> selectList() {
        return crontabInfoService.selectList();
    }

    @Log(title = "更新定时任务Crontab表达式", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "更新定时任务Crontab表达式")
    @PostMapping("/updateCron")
    public void updateCronExpression(@Valid @RequestBody CrontabUpdateDTO crontabUpdateDTO) {
        crontabInfoService.updateCronExpression(crontabUpdateDTO);
    }

}
