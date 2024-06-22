package org.travis.center.support.service;

import org.quartz.SchedulerException;
import org.travis.center.support.pojo.dto.QuartzCreateParamDTO;
import org.travis.center.support.pojo.dto.QuartzJobKeyDTO;
import org.travis.center.support.pojo.dto.QuartzUpdateParamDTO;
import org.travis.center.support.pojo.vo.QuartzJobDetailVO;

import java.util.List;

/**
 * @ClassName QuartzService
 * @Description QuartzService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/22
 */
public interface QuartzService {
    /**
     * 添加定时任务
     */
    void addJob(QuartzCreateParamDTO param) throws SchedulerException;

    /**
     * 修改定时任务
     */
    void updateJob(QuartzUpdateParamDTO param) throws SchedulerException;

    /**
     * 暂停定时任务
     */
    void pauseJob(QuartzJobKeyDTO param) throws SchedulerException;

    /**
     * 恢复定时任务
     */
    void resumeJob(QuartzJobKeyDTO param) throws SchedulerException;

    /**
     * 删除定时任务
     */
    void deleteJob(QuartzJobKeyDTO param) throws SchedulerException;

    /**
     * 定时任务列表
     */
    List<QuartzJobDetailVO> jobList() throws SchedulerException;

    /**
     * 定时任务详情
     */
    QuartzJobDetailVO jobDetail(QuartzJobKeyDTO param) throws SchedulerException;
}
