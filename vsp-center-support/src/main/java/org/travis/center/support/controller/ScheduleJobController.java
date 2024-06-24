package org.travis.center.support.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.travis.center.common.entity.support.ScheduleJob;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* (VSP.VSP_SCHEDULE_JOB)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/jobs")
public class ScheduleJobController {

    @Resource
    private ScheduleJobService scheduleJobService;


    @Log(title = "查询定时任务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询定时任务列表")
    @GetMapping("/list")
    public List<ScheduleJob> selectList() {
        return scheduleJobService.selectList();
    }

}
