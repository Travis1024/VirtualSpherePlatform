package org.travis.center.support.controller;

import org.travis.center.support.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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



}
