package org.travis.center.support.service;

import org.quartz.SchedulerException;
import org.travis.center.common.entity.support.ScheduleJob;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.support.pojo.dto.ScheduleJobCreateDTO;
import org.travis.center.support.pojo.dto.ScheduleJobUpdateDTO;
import org.travis.center.support.pojo.vo.QuartzJobDetailVO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.util.List;

/**
 * @ClassName ScheduleJobService
 * @Description ScheduleJobService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
public interface ScheduleJobService extends IService<ScheduleJob> {

    ScheduleJob createScheduleJob(ScheduleJobCreateDTO jobCreateDTO) throws SchedulerException;

    void updateScheduleJob(ScheduleJobUpdateDTO jobUpdateDTO) throws SchedulerException;

    void deleteScheduleJob(Long jobId) throws SchedulerException;

    List<ScheduleJob> selectList();

    PageResult<ScheduleJob> pageSelectList(PageQuery pageQuery);

    void pauseJob(Long jobId) throws SchedulerException;

    void resumeJob(Long jobId) throws SchedulerException;

    QuartzJobDetailVO queryScheduleJobDetails(Long jobId) throws SchedulerException;
}
