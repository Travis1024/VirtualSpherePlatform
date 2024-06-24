package org.travis.agent.web.crontab;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.travis.agent.web.handler.VmwareStateAggregateHandler;
import org.travis.agent.web.utils.DubboAddrUtil;
import org.travis.api.client.center.CenterHealthyClient;
import org.travis.api.pojo.bo.HostHealthyStateBO;
import org.travis.shared.common.constants.ScheduleJobConstant;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.DubboFunctionException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @ClassName CrontabConfigService
 * @Description CrontabConfigService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Slf4j
@Component
public class CrontabScheduleService implements SchedulingConfigurer {

    @Resource
    public RedissonClient redissonClient;
    @DubboReference(
            methods = {
                    @Method(name = "dubboHealthyCheck", timeout = 2000, retries = 2),
                    @Method(name = "pushHostHealthyState", timeout = 4000)
            }
    )
    public CenterHealthyClient centerHealthyClient;
    @Resource
    public DubboAddrUtil dubboAddrUtil;
    @Resource
    public VmwareStateAggregateHandler vmwareStateAggregateHandler;


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 在注册器添加定时任务前添加线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(1));

        // 1.[健康状态推送] 定时任务
        taskRegistrar.addTriggerTask(
                // 1.获取所有缓存操作日志列表
                this::operateHandleHealthyStates,
                // 2.设置任务执行周期
                triggerContext -> new CronTrigger(ScheduleJobConstant.CRON_10_S).nextExecutionTime(triggerContext)
        );
    }

    private void operateHandleHealthyStates() {
        try {
            String uuid = IdUtil.fastSimpleUUID();
            log.info("[Healthy-Push-Task-Start-{}] Healthy States Push Crontab Schedule Started", uuid);
            // 1.Dubbo-检测 Center 健康状态
            R<Void> centerHealthyCheckR = centerHealthyClient.dubboHealthyCheck();
            Assert.isTrue(centerHealthyCheckR.checkSuccess(), () -> new DubboFunctionException(centerHealthyCheckR.getMsg()));

            // 2.获取虚拟机状态信息
            Map<String, String> vmwareUuidStateMap = vmwareStateAggregateHandler.queryVmwareUuidStatesMap();

            // 3.组装参数
            HostHealthyStateBO hostHealthyStateBO = new HostHealthyStateBO();
            hostHealthyStateBO.setHostIp(dubboAddrUtil.getRegisterToDubboIpAddr());
            hostHealthyStateBO.setVmwareUuidStateMap(vmwareUuidStateMap);
            hostHealthyStateBO.setRecordTime(System.currentTimeMillis());

            // 4.sleep random seconds
            Thread.sleep(RandomUtil.randomInt(15, 1500));

            // 5.Dubbo-推送健康状态
            R<Void> pushR = centerHealthyClient.pushHostHealthyState(hostHealthyStateBO);
            Assert.isTrue(pushR.checkSuccess(), () -> new DubboFunctionException(pushR.getMsg()));

            log.info("[Healthy-Push-Task-Start-{}] Healthy States Push Finished!", uuid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
