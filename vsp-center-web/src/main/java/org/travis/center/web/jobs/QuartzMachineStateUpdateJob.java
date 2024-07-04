package org.travis.center.web.jobs;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.HostStateEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.constants.SystemConstant;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName QuartzMachineStateUpdateJob
 * @Description 宿主机虚拟机状态定时更新任务
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/25
 */
@Slf4j
@Component
public class QuartzMachineStateUpdateJob extends QuartzJobBean {

    @Resource
    public RedissonClient redissonClient;
    @Resource
    public HostInfoMapper hostInfoMapper;
    @Resource
    public VmwareInfoMapper vmwareInfoMapper;

    private static final Long MILLISECOND_30S = 30 * 1000L;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        operateMachineStateUpdateHandle();
    }

    private void operateMachineStateUpdateHandle() {
        // 1.程序启动预热期判断
        RBucket<Long> rBucket = redissonClient.getBucket(SystemConstant.PROGRAM_START_TIME_KEY);
        Long startTime = Optional.ofNullable(rBucket.get()).orElse(System.currentTimeMillis());

        if (ObjectUtil.isNull(startTime)) {
            log.error("[CrontabScheduleService::operateMachineStateUpdateHandle] No start time is found!");
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - startTime <= 60000) {
            log.warn("[[CrontabScheduleService::operateMachineStateUpdateHandle] Program warming up···");
            return;
        }

        log.info("[Crontab-Task-Start] Machines state update crontab schedule started");

        // 2.宿主机状态判断及修改
        List<HostInfo> hostInfoList = hostInfoMapper.selectList(Wrappers.<HostInfo>lambdaQuery().select(HostInfo::getId, HostInfo::getIp, HostInfo::getState));
        for (HostInfo hostInfo : hostInfoList) {
            RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet(RedissonConstant.HEALTHY_HOST_RECORDS + hostInfo.getIp());

            // 2.1.判断是否健康
            Collection<String> valuedRange = sortedSet.valueRange(currentTimeMillis - MILLISECOND_30S, true, currentTimeMillis, true);
            boolean isHealthy = !valuedRange.isEmpty();
            // 删除历史数据
            sortedSet.removeRangeByScore(0, true, currentTimeMillis - MILLISECOND_30S, true);

            // 2.1.「准备中」or「停用」or「初始化异常」状态忽略，不进行处理
            if (HostStateEnum.IN_PREPARATION.equals(hostInfo.getState()) || HostStateEnum.DISABLE.equals(hostInfo.getState()) || HostStateEnum.INIT_ERROR.equals(hostInfo.getState())) {
                continue;
            }

            // 2.2.「就绪」状态 + 无心跳，修改状态为「心跳异常」
            if (!isHealthy && HostStateEnum.READY.equals(hostInfo.getState())) {
                hostInfoMapper.update(Wrappers.<HostInfo>lambdaUpdate().set(HostInfo::getState, HostStateEnum.HEART_BEAT_ERROR).eq(HostInfo::getId, hostInfo.getId()));
            }

            // 2.3.「心跳异常」状态 + 有心跳，修改状态为「就绪」
            if (isHealthy && HostStateEnum.HEART_BEAT_ERROR.equals(hostInfo.getState())) {
                hostInfoMapper.update(Wrappers.<HostInfo>lambdaUpdate().set(HostInfo::getState, HostStateEnum.READY).eq(HostInfo::getId, hostInfo.getId()));
            }
        }

        // 3.虚拟机状态判断及修改
        List<VmwareInfo> vmwareInfoList = vmwareInfoMapper.selectList(Wrappers.<VmwareInfo>lambdaQuery().select(VmwareInfo::getId, VmwareInfo::getUuid, VmwareInfo::getState));
        RMap<String, String> rMap = redissonClient.getMap(RedissonConstant.HEALTHY_VMWARE_RECORDS);
        for (VmwareInfo vmwareInfo : vmwareInfoList) {
            // 3.1.获取虚拟机真实状态信息 + 删除当前状态信息
            String vmwareState = rMap.get(vmwareInfo.getUuid());
            rMap.remove(vmwareInfo.getUuid());

            if (StrUtil.isBlank(vmwareState)) {
                if (!VmwareStateEnum.HEART_BEAT_ERROR.equals(vmwareInfo.getState())) {
                    vmwareInfoMapper.update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, VmwareStateEnum.HEART_BEAT_ERROR).eq(VmwareInfo::getId, vmwareInfo.getId()));
                }
                continue;
            }
            VmwareStateEnum vmwareStateEnum = VmwareStateEnum.ofTag(vmwareState);
            if (ObjectUtil.isNull(vmwareStateEnum)) {
                if (!VmwareStateEnum.UNKNOW.equals(vmwareInfo.getState())) {
                    vmwareInfoMapper.update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, VmwareStateEnum.UNKNOW).eq(VmwareInfo::getId, vmwareInfo.getId()));
                }
                continue;
            }
            // 3.2.判断真实状态是否和数据库中相同
            if (!vmwareStateEnum.equals(vmwareInfo.getState())) {
                vmwareInfoMapper.update(Wrappers.<VmwareInfo>lambdaUpdate().set(VmwareInfo::getState, vmwareStateEnum).eq(VmwareInfo::getId, vmwareInfo.getId()));
            }
        }

        log.info("[Crontab-Task-Finished] Machines state update finished.");
    }
}
