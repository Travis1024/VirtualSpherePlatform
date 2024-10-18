package org.travis.center.monitor.threads.addition;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.platform.common.constants.AlarmConstant;
import com.hust.platform.common.constants.MonitorConstant;
import com.hust.platform.common.constants.StatisticConstant;
import com.hust.platform.common.entity.AlarmInfo;
import com.hust.platform.common.entity.ThresholdInfo;
import com.hust.platform.common.mapper.AlarmInfoMapper;
import com.hust.platform.common.mapper.ThresholdInfoMapper;
import com.hust.platform.common.utils.ApplicationContextUtil;
import com.hust.platform.common.websocket.WebSocketAlarmInfo;
import com.hust.platform.logger.service.LogInfoService;
import com.hust.platform.logger.threads.TaskThreadNumberStatistic;
import com.hust.platform.monitor.service.AlarmRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AlarmMonitorThread
 * @Description 报警信息监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/12
 */
@Slf4j
public class ThreadAlarmMonitor implements Runnable{

    private String uuid;
    private String jsonStr;
    private Long alarmIntervalTime;
    private AlarmInfoMapper alarmInfoMapper;
    private ThresholdInfoMapper thresholdInfoMapper;
    private LogInfoService logInfoService;
    private ThreadPoolExecutor threadPoolExecutor;
    private RedisTemplate redisTemplate;

    private Map<String, ThresholdInfo> thresholdInfoMap = null;
    private AlarmRuleService alarmRuleOneService;
    private AlarmRuleService alarmRuleTwoService;

    public ThreadAlarmMonitor(String uuid, String jsonStr, Long alarmIntervalTime) {
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.alarmIntervalTime = alarmIntervalTime;
        this.alarmInfoMapper = ApplicationContextUtil.getBean(AlarmInfoMapper.class);
        this.thresholdInfoMapper = ApplicationContextUtil.getBean(ThresholdInfoMapper.class);
        this.logInfoService = ApplicationContextUtil.getBean(LogInfoService.class);
        this.threadPoolExecutor = ApplicationContextUtil.getBean(ThreadPoolExecutor.class);
        this.redisTemplate = ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
        this.alarmRuleOneService = ApplicationContextUtil.getBean("one", AlarmRuleService.class);
        this.alarmRuleTwoService = ApplicationContextUtil.getBean("two", AlarmRuleService.class);
    }


    private void execAlarm(String value, String targetTag, Date beginTime) {
        // 一、判断 3 分钟之内当前主机是否存在相同标签的报警消息, 如果存在，继续运行；不存在则设置 3 分钟报警标志 + WebSocket 推送报警信息
        Boolean alarmFlag = redisTemplate.opsForValue().setIfAbsent(AlarmConstant.FLAG_PREFIX + uuid + AlarmConstant.MH + targetTag, AlarmConstant.FLAG, alarmIntervalTime, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(alarmFlag)) {
            WebSocketAlarmInfo.sendAlarmMessage(uuid, "[报警信息] uuid:" + uuid + " | " + targetTag + " --> " + value);
        }
        // 二、判断「当前指标」的上一次报警是否已经结束，即 redis 中是否还存在「当前指标」的报警信息
        Boolean hassedKey = redisTemplate.hasKey(AlarmConstant.RECORD_PREFIX + uuid + AlarmConstant.MH + targetTag);
        // 存在「当前指标」的报警信息，return
        if (Boolean.TRUE.equals(hassedKey)) {
            return;
        }
        // 三、不存在报警消息，数据库写入新报警记录 + redis 写入报警消息
        // 数据量写入新报警记录
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setAlarmMachineUuid(uuid);
        alarmInfo.setAlarmTargetTag(targetTag);
        alarmInfo.setAlarmValue(value);
        alarmInfo.setAlarmBeginTime(beginTime);
        alarmInfoMapper.insert(alarmInfo);
        // 如果不存在「当前指标」的报警信息，则 redis 写入报警消息
        redisTemplate.opsForValue().setIfAbsent(AlarmConstant.RECORD_PREFIX + uuid + AlarmConstant.MH + targetTag, JSONUtil.toJsonStr(alarmInfo));

    }

    private void cancelAlarm(String targetTag, Date endTime) {
        // 判断 redis 中是否存在此类型的报警记录, 如果不存在 continue，存在则更新数据库中报警结束时间
        Boolean hassedKey = redisTemplate.hasKey(AlarmConstant.RECORD_PREFIX + uuid + AlarmConstant.MH + targetTag);
        if (Boolean.FALSE.equals(hassedKey)) {
            return;
        }
        // 如果存在，获取 value 中的自增ID、修改数据库中的报警结束时间、删除kv缓存
        Object object = redisTemplate.opsForValue().get(AlarmConstant.RECORD_PREFIX + uuid + AlarmConstant.MH + targetTag);
        if (object != null) {
            AlarmInfo alarmInfo = JSONUtil.toBean(object.toString(), AlarmInfo.class);
            UpdateWrapper<AlarmInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(AlarmInfo.ALARM_ZZID, alarmInfo.getAlarmZzid()).set(AlarmInfo.ALARM_END_TIME, endTime);
            alarmInfoMapper.update(null, updateWrapper);
        }
        redisTemplate.delete(AlarmConstant.RECORD_PREFIX + uuid + AlarmConstant.MH + targetTag);
    }

    private Map<String, ThresholdInfo> queryThrvMapByUuid() {
        // 判断 redis 中是否有缓存
        Object object = redisTemplate.opsForValue().get(AlarmConstant.KEY_PREFIX + uuid);

        if (object != null) {
            Map<String, ThresholdInfo> result = new HashMap<>();
            JSONObject jsonObject = JSONUtil.parseObj(object);
            for (Map.Entry<String, Object> objectEntry : jsonObject) {
                ThresholdInfo thresholdInfo = JSONUtil.toBean(objectEntry.getValue().toString(), ThresholdInfo.class);
                result.put(objectEntry.getKey(), thresholdInfo);
            }
            return result;
        }

        // 无缓存，从数据库中查询
        Map<String, ThresholdInfo> resultMap = new HashMap<>();
        List<ThresholdInfo> thresholdInfoList = thresholdInfoMapper.selectList(Wrappers.<ThresholdInfo>lambdaQuery().eq(ThresholdInfo::getThrvMachineUuid, uuid));

        for (ThresholdInfo thresholdInfo : thresholdInfoList) {
            resultMap.put(thresholdInfo.getThrvTargetTag(), thresholdInfo);
        }

        // 将查询数据缓存到 redis 中
        log.info("数据库数据");
        redisTemplate.opsForValue().set(AlarmConstant.KEY_PREFIX + uuid, JSONUtil.toJsonStr(resultMap));
        return resultMap;
    }

    private boolean judgeRule(String targetTag) {
        ThresholdInfo thresholdInfo = this.thresholdInfoMap.get(targetTag);
        if (Integer.valueOf(1).equals(thresholdInfo.getThrvRuleType())) {
            return alarmRuleOneService.judgeAlarmRule(uuid, targetTag, thresholdInfo);
        } else if (Integer.valueOf(2).equals(thresholdInfo.getThrvRuleType())) {
            return alarmRuleTwoService.judgeAlarmRule(uuid, targetTag, thresholdInfo);
        }
        return false;
    }

    private void breakJudgeRule(String targetTag) {
        ThresholdInfo thresholdInfo = this.thresholdInfoMap.get(targetTag);
        if (Integer.valueOf(1).equals(thresholdInfo.getThrvRuleType())) {
            alarmRuleOneService.breakRule(uuid, targetTag);
        } else if (Integer.valueOf(2).equals(thresholdInfo.getThrvRuleType())) {
            alarmRuleTwoService.breakRule(uuid, targetTag);
        }
    }


    @Override
    public void run() {
        try {
            // 获取当前主机需要监控的指标值
            Map<String, ThresholdInfo> pairMap = queryThrvMapByUuid();
            this.thresholdInfoMap = pairMap;
            if (pairMap.isEmpty()) {
                log.info("[Alarm 监测线程执行结束] -> " + uuid);
                return;
            }

            // 解析 json 数据, 获取其中的可能报警值列表
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            JsonNode alarmListNode = rootNode.get(MonitorConstant.ALERT_STAT);
            long timestamp = rootNode.get(MonitorConstant.TIMESTAMP).asLong();

            log.info("================= {} =================", uuid);
            log.info(JSONUtil.toJsonStr(pairMap));
            log.info(alarmListNode.toString());
            log.info("================= END =================");

            // 循环判断是否超出阈值限制
            for (String targetTag : pairMap.keySet()) {
                ThresholdInfo thresholdInfo = pairMap.get(targetTag);
                Integer thrvType = thresholdInfo.getThrvType();

                if (thrvType == AlarmConstant.HIGH) {
                    double highValue = alarmListNode.get(targetTag).asDouble();
                    double highLimit = thresholdInfo.getThrvHighLimit();
                    // 判断是否超出阈值
                    if (highValue > highLimit) {
                        // 超出阈值，判断是否需要报警
                        if (judgeRule(targetTag)) {
                            execAlarm(String.valueOf(highValue), targetTag, new Date(timestamp * 1000L));
                        }
                    } else {
                        // 无需报警
                        breakJudgeRule(targetTag);
                        cancelAlarm(targetTag, new Date(timestamp * 1000L));
                    }
                } else if (thrvType == AlarmConstant.LOW) {
                    double lowValue = alarmListNode.get(targetTag).asDouble();
                    double lowLimit = thresholdInfo.getThrvLowLimit();
                    // 判断是否超出阈值
                    if (lowValue < lowLimit) {
                        // 超出阈值，判断是否需要报警
                        if (judgeRule(targetTag)) {
                            execAlarm(String.valueOf(lowValue), targetTag, new Date(timestamp * 1000L));
                        }
                    } else {
                        // 无需报警
                        breakJudgeRule(targetTag);
                        cancelAlarm(targetTag, new Date(timestamp * 1000L));
                    }
                } else if (thrvType == AlarmConstant.BOOL) {
                    boolean boolValue = alarmListNode.get(targetTag).asBoolean();
                    boolean boolLimit = thresholdInfo.getThrvBoolValue();
                    // 增加需要报警的判断规则逻辑
                    if (boolValue == boolLimit && judgeRule(targetTag)) {
                        // 超出阈值，判断是否需要报警
                        if (judgeRule(targetTag)) {
                            execAlarm(String.valueOf(boolValue), targetTag, new Date(timestamp * 1000L));
                        }
                    } else {
                        // 无需报警
                        breakJudgeRule(targetTag);
                        cancelAlarm(targetTag, new Date(timestamp * 1000L));
                    }
                }
            }

            log.info("[Alarm 监测线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.ALARM_THREAD, true));
        } catch (Exception e) {
            log.error(e.toString());
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.ALARM_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }
    }
}
