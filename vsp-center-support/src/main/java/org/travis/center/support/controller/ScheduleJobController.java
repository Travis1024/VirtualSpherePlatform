package org.travis.center.support.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.quartz.SchedulerException;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.support.ScheduleJob;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.support.aspect.Log;
import org.travis.center.support.aspect.RequestLock;
import org.travis.center.support.aspect.RequestLockKey;
import org.travis.center.support.pojo.dto.ScheduleJobUpdateDTO;
import org.travis.center.support.pojo.vo.QuartzJobDetailVO;
import org.travis.center.support.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

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

    @Log(title = "分页查询定时任务列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询定时任务列表")
    @PostMapping("/pageList")
    public PageResult<ScheduleJob> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return scheduleJobService.pageSelectList(pageQuery);
    }

    @RequestLock
    @Log(title = "更新定时任务Cron表达式", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "更新定时任务Cron表达式")
    @PutMapping("/update")
    public void updateScheduleJob(@Validated @RequestBody ScheduleJobUpdateDTO jobUpdateDTO) throws SchedulerException {
        scheduleJobService.updateScheduleJob(jobUpdateDTO);
    }

    @RequestLock
    @Log(title = "删除定时任务", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "删除定时任务")
    @DeleteMapping("/delete")
    public void deleteScheduleJob(@RequestLockKey Long jobId) throws SchedulerException {
        scheduleJobService.deleteScheduleJob(jobId);
    }

    @Log(title = "查询单个定时任务的详细信息", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询单个定时任务的详细信息")
    @GetMapping("/details")
    public QuartzJobDetailVO queryScheduleJobDetails(Long jobId) throws SchedulerException {
        return scheduleJobService.queryScheduleJobDetails(jobId);
    }

    @RequestLock
    @Log(title = "暂停定时任务", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "暂停定时任务")
    @PutMapping("/pause")
    public void pauseJob(@RequestLockKey Long jobId) throws SchedulerException {
        scheduleJobService.pauseJob(jobId);
    }

    @RequestLock
    @Log(title = "恢复定时任务", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "恢复定时任务")
    @PutMapping("/resume")
    public void resumeJob(@RequestLockKey Long jobId) throws SchedulerException {
        scheduleJobService.resumeJob(jobId);
    }
}
