package org.travis.center.monitor.threads.addition;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.hust.platform.common.constants.AlarmConstant;
import com.hust.platform.common.constants.MonitorConstant;
import com.hust.platform.common.constants.StatisticConstant;
import com.hust.platform.common.entity.AlarmInfo;
import com.hust.platform.common.mapper.AlarmInfoMapper;
import com.hust.platform.common.pojo.monitor.vo.TriggerInfoVO;
import com.hust.platform.common.utils.ApplicationContextUtil;
import com.hust.platform.common.websocket.WebSocketAlarmInfo;
import com.hust.platform.logger.service.LogInfoService;
import com.hust.platform.logger.threads.TaskThreadNumberStatistic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TaskTriggerMonitorThread
 * @Description 报警触发事件监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Slf4j
public class ThreadTriggerMonitor implements Runnable{

    private String uuid;
    private String jsonStr;
    private LogInfoService logInfoService;
    private Long alarmIntervalTime;
    private ThreadPoolExecutor threadPoolExecutor;
    private AlarmInfoMapper alarmInfoMapper;
    private RedisTemplate redisTemplate;

    public ThreadTriggerMonitor(String uuid, String jsonStr, Long alarmIntervalTime) {
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.alarmIntervalTime = alarmIntervalTime;
        this.logInfoService = ApplicationContextUtil.getBean(LogInfoService.class);
        this.threadPoolExecutor = ApplicationContextUtil.getBean(ThreadPoolExecutor.class);
        this.alarmInfoMapper = ApplicationContextUtil.getBean(AlarmInfoMapper.class);
        this.redisTemplate = ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
    }


    private String saveTriggerInfo() {
        TriggerInfoVO triggerInfoVO = JSONUtil.toBean(jsonStr, TriggerInfoVO.class);
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setAlarmMachineUuid(uuid);
        alarmInfo.setAlarmTargetTag(MonitorConstant.TRIGGER);
        String alarmValue = triggerInfoVO.getKey() + " | " + triggerInfoVO.getValue();
        alarmInfo.setAlarmValue(alarmValue.length() > 1024 ? alarmValue.substring(0, 1024) : alarmValue);
        Date date = new Date(triggerInfoVO.getTimestamp() * 1000L);
        alarmInfo.setAlarmBeginTime(date);
        alarmInfo.setAlarmEndTime(date);
        alarmInfoMapper.insert(alarmInfo);
        return alarmValue;
    }

    public void sendWebSocket(String alarmValue) {
        // 判断 3 分钟之内是否存在报警消息, 如果存在，继续运行；不存在则设置 3 分钟报警标志 + WebSocket 推送报警信息
        Boolean alarmFlag = redisTemplate.opsForValue().setIfAbsent(AlarmConstant.TRIGGER_FLAG_PREFIX + uuid, AlarmConstant.FLAG, alarmIntervalTime, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(alarmFlag)) {
            WebSocketAlarmInfo.sendAlarmMessage(uuid, "[Trigger-报警信息] uuid:" + uuid + " | " + alarmValue);
        }
    }

    @Override
    public void run() {
        try {
            // 解析 json 数据，并插入到数据库中
            String alarmValue = saveTriggerInfo();
            // 向前端推送 触发事件告警信息
            sendWebSocket(alarmValue);

            log.info("[Trigger 监测线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.TRIGGER_THREAD, true));
        } catch (Exception e) {
            log.error(e.toString());
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.TRIGGER_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }
    }
}
