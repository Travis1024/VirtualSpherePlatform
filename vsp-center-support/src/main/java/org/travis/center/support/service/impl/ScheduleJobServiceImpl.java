package org.travis.center.support.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.enums.ScheduleStatusEnum;
import org.travis.center.common.mapper.support.ScheduleJobMapper;
import org.travis.center.common.entity.support.ScheduleJob;
import org.travis.center.support.pojo.dto.*;
import org.travis.center.support.pojo.vo.QuartzJobDetailVO;
import org.travis.center.support.service.QuartzService;
import org.travis.center.support.service.ScheduleJobService;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.exceptions.NotFoundException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName ScheduleJobServiceImpl
 * @Description ScheduleJobServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Service
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Resource
    private QuartzService quartzService;

    @Transactional
    @Override
    public ScheduleJob createScheduleJob(@Valid ScheduleJobCreateDTO jobCreateDTO) throws SchedulerException {
        // 1.校验 Crontab 表达式是否合法
        Assert.isTrue(CronExpression.isValidExpression(jobCreateDTO.getCronExpression()), () -> new BadRequestException("Crontab 表达式校验失败!"));

        // 2.初始化 scheduleJob
        ScheduleJob scheduleJob = new ScheduleJob();
        BeanUtils.copyProperties(jobCreateDTO, scheduleJob);
        scheduleJob.setScheduleStatus(ScheduleStatusEnum.RUNNING);
        // 不指定 ID 则自动生成
        scheduleJob.setId(ObjectUtil.isEmpty(scheduleJob.getId()) ? SnowflakeIdUtil.nextId() : scheduleJob.getId());
        save(scheduleJob);

        // 3.初始化 QuartzCreateParamDTO + 创建JOB (新增 + 启动)
        QuartzCreateParamDTO createParamDTO = getQuartzCreateParamDTO(scheduleJob, jobCreateDTO.getJobDataMap());
        quartzService.addJob(createParamDTO);

        return scheduleJob;
    }

    private static QuartzCreateParamDTO getQuartzCreateParamDTO(ScheduleJob scheduleJob, Map<String, Object> jobDataMap) {
        QuartzCreateParamDTO createParamDTO = new QuartzCreateParamDTO();
        createParamDTO.setJobGroup(scheduleJob.getJobGroup().getTag());
        createParamDTO.setJobName(scheduleJob.getId().toString());
        createParamDTO.setJobClazz(scheduleJob.getJobClass());
        createParamDTO.setJobDescription(scheduleJob.getScheduleName());
        createParamDTO.setTriggerCrontab(scheduleJob.getCronExpression());
        createParamDTO.setJobDataMap(jobDataMap);
        return createParamDTO;
    }

    @Transactional
    @Override
    public void updateScheduleJob(@Valid ScheduleJobUpdateDTO jobUpdateDTO) throws SchedulerException {
        // 1.查询定时任务信息，按断判断是否允许修改
        Optional<ScheduleJob> scheduleJobOptional = Optional.ofNullable(getById(jobUpdateDTO.getId()));
        ScheduleJob scheduleJob = scheduleJobOptional.orElseThrow(() -> new NotFoundException("未找到相关定时任务!"));
        Assert.isTrue(IsFixedEnum.ALLOW_UPDATE.equals(scheduleJob.getIsFixed()), () -> new ForbiddenException("当前定时任务 Crontab 表达式禁止修改!"));
        // 2.校验 CronExpression 表达式是否合法
        Assert.isTrue(CronExpression.isValidExpression(jobUpdateDTO.getCronExpression()), () -> new BadRequestException("Crontab 表达式校验失败!"));
        // 3.更新表达式
        // 3.1.更新数据库
        getBaseMapper().update(
                Wrappers.<ScheduleJob>lambdaUpdate()
                        .set(ScheduleJob::getCronExpression, jobUpdateDTO.getCronExpression())
                        .set(ScheduleJob::getCronDescription, jobUpdateDTO.getCronDescription())
                        .eq(ScheduleJob::getId, jobUpdateDTO.getId())
        );
        // 3.2.更新 Quartz
        QuartzUpdateParamDTO quartzUpdateParamDTO = new QuartzUpdateParamDTO();
        quartzUpdateParamDTO.setJobGroup(scheduleJob.getJobGroup().getTag());
        quartzUpdateParamDTO.setJobName(scheduleJob.getId().toString());
        quartzUpdateParamDTO.setTriggerCrontab(jobUpdateDTO.getCronExpression());
        quartzService.updateJob(quartzUpdateParamDTO);
    }

    @Transactional
    @Override
    public void deleteScheduleJob(Long jobId) throws SchedulerException {
        // 1.查询定时任务信息
        Optional<ScheduleJob> scheduleJobOptional = Optional.ofNullable(getById(jobId));
        ScheduleJob scheduleJob = scheduleJobOptional.orElseThrow(() -> new NotFoundException("未找到相关定时任务!"));

        // 2.删除定时任务
        removeById(jobId);

        // 3.删除 Quartz
        QuartzJobKeyDTO quartzJobKeyDTO = new QuartzJobKeyDTO();
        quartzJobKeyDTO.setJobGroup(scheduleJob.getJobGroup().getTag());
        quartzJobKeyDTO.setJobName(jobId.toString());
        quartzService.deleteJob(quartzJobKeyDTO);
    }

    @Override
    public List<ScheduleJob> selectList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<ScheduleJob> pageSelectList(PageQuery pageQuery) {
        Page<ScheduleJob> scheduleJobPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(scheduleJobPage);
    }

    @Transactional
    @Override
    public void pauseJob(Long jobId) throws SchedulerException {
        // 1.查询定时任务信息
        ScheduleJob scheduleJob = Optional.ofNullable(getById(jobId)).orElseThrow(() -> new NotFoundException("未找到相关定时任务!"));

        // 2.修改数据库任务状态
        getBaseMapper().update(
                Wrappers.<ScheduleJob>lambdaUpdate()
                        .set(ScheduleJob::getScheduleStatus, ScheduleStatusEnum.STOPPING)
                        .eq(ScheduleJob::getId, jobId)
        );

        // 3.暂停定时任务
        QuartzJobKeyDTO quartzJobKeyDTO = new QuartzJobKeyDTO();
        quartzJobKeyDTO.setJobGroup(scheduleJob.getJobGroup().getTag());
        quartzJobKeyDTO.setJobName(jobId.toString());
        quartzService.pauseJob(quartzJobKeyDTO);
    }

    @Transactional
    @Override
    public void resumeJob(Long jobId) throws SchedulerException {
        // 1.查询定时任务信息
        ScheduleJob scheduleJob = Optional.ofNullable(getById(jobId)).orElseThrow(() -> new NotFoundException("未找到相关定时任务!"));

        // 2.修改数据库任务状态
        getBaseMapper().update(
                Wrappers.<ScheduleJob>lambdaUpdate()
                        .set(ScheduleJob::getScheduleStatus, ScheduleStatusEnum.RUNNING)
                        .eq(ScheduleJob::getId, jobId)
        );

        // 3.暂停定时任务
        QuartzJobKeyDTO quartzJobKeyDTO = new QuartzJobKeyDTO();
        quartzJobKeyDTO.setJobGroup(scheduleJob.getJobGroup().getTag());
        quartzJobKeyDTO.setJobName(jobId.toString());
        quartzService.resumeJob(quartzJobKeyDTO);
    }

    @Override
    public QuartzJobDetailVO queryScheduleJobDetails(Long jobId) throws SchedulerException {
        // 1.查询定时任务信息
        ScheduleJob scheduleJob = Optional.ofNullable(getById(jobId)).orElseThrow(() -> new NotFoundException("未找到相关定时任务!"));

        // 2.查询详细信息
        QuartzJobKeyDTO quartzJobKeyDTO = new QuartzJobKeyDTO();
        quartzJobKeyDTO.setJobGroup(scheduleJob.getJobGroup().getTag());
        quartzJobKeyDTO.setJobName(jobId.toString());
        return quartzService.jobDetail(quartzJobKeyDTO);
    }
}
