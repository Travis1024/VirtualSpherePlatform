package org.travis.center.monitor.threads.addition;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hust.platform.common.constants.MonitorConstant;
import com.hust.platform.common.constants.StatisticConstant;
import com.hust.platform.common.entity.ServiceMonitor;
import com.hust.platform.common.utils.ApplicationContextUtil;
import com.hust.platform.common.utils.R;
import com.hust.platform.common.utils.RedisUtil;
import com.hust.platform.logger.service.LogInfoService;
import com.hust.platform.logger.threads.TaskThreadNumberStatistic;
import com.hust.platform.monitor.service.ServiceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadServiceMonitor
 * @Description 服务质量监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/1/4
 */
@Slf4j
public class ThreadServiceMonitor implements Runnable{
    private String uuid;
    private String jsonStr;
    private StringRedisTemplate stringRedisTemplate;
    private RedisUtil redisUtil;
    private LogInfoService logInfoService;
    private ThreadPoolExecutor threadPoolExecutor;
    private ServiceMonitorService serviceMonitorService;

    public ThreadServiceMonitor(String uuid, String jsonStr) {
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.stringRedisTemplate = ApplicationContextUtil.getBean(StringRedisTemplate.class);
        this.redisUtil = ApplicationContextUtil.getBean(RedisUtil.class);
        this.logInfoService = ApplicationContextUtil.getBean(LogInfoService.class);
        this.threadPoolExecutor = ApplicationContextUtil.getBean(ThreadPoolExecutor.class);
        this.serviceMonitorService = ApplicationContextUtil.getBean(ServiceMonitorService.class);
    }

    private int calcHealthScore(ServiceMonitor serviceMonitor, Long rssMemorySize, long totalMemorySize) {
        double rate = 100.0 * rssMemorySize / totalMemorySize;
        if (rate < serviceMonitor.getServiceMemLimitRate()) {
            return (int) ((serviceMonitor.getServiceMemLimitRate() - rate) * 100.0 / serviceMonitor.getServiceCpuLimitRate());
        }
        return 100;
    }

    private void judgeHealth(ServiceMonitor serviceMonitor, int healthScore) {
        if (healthScore < serviceMonitor.getServiceHealthLimitScore()) {
            // 非健康，低于健康值最低阈值
            Long sumValue = stringRedisTemplate.opsForValue().increment(MonitorConstant.SERVICE_SUM_PREFIX + StrUtil.COLON + uuid + StrUtil.COLON + serviceMonitor.getServiceName(), 1);
            log.warn("连续非健康次数：{}", sumValue);
            if (sumValue == null) {
                return;
            }
            if (sumValue == 3) {
                R<?> execkedReplace = serviceMonitorService.execReplace(uuid, serviceMonitor, healthScore);
                if (!R.checkSuccess(execkedReplace)) {
                    log.error("[服务替换执行失败] - 失败原因：" + JSONUtil.toJsonStr(execkedReplace));
                }
            }
        } else {
            // 健康
            stringRedisTemplate.delete(MonitorConstant.SERVICE_SUM_PREFIX + StrUtil.COLON + uuid + StrUtil.COLON + serviceMonitor.getServiceName());
        }
    }

    @Override
    public void run() {
        try {
            // 1、判断是否含有当前节点服务的监控记录
            Set<String> keySet = redisUtil.scan(MonitorConstant.SERVICE_PREFIX + StrUtil.COLON + uuid + "*");
            if (CollUtil.isEmpty(keySet)) {
                return;
            }

            // 2、解析所有进程信息
            // 2.1、将 json 字符串转为 JsonNode 节点，并提取其中的 process-stat 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            JsonNode processNode = rootNode.get(MonitorConstant.PROCESS_STAT).get(MonitorConstant.PROCESS_PROCESSES);
            ArrayNode processNodeArray = (ArrayNode) processNode;
            // 2.2、获取当前节点的内存容量
            long totalMemorySize = rootNode.get(MonitorConstant.MEM_STAT).get("virtual_memory_stat").get("total").asLong();

            // 2.3、循环解析，添加进 Map
            Map<Integer, Long> map = new HashMap<>(processNodeArray.size());
            for (JsonNode jsonNode : processNodeArray) {
                int pid = jsonNode.get("pid").asInt();
                long rss = jsonNode.get("mem_info").get("rss").asLong();
                map.put(pid, rss);
            }

            // 3、含有当前节点监控记录
            for (String oneKey : keySet) {
                String value = stringRedisTemplate.opsForValue().get(oneKey);
                ServiceMonitor serviceMonitor = JSONUtil.toBean(value, ServiceMonitor.class);
                if (!map.containsKey(serviceMonitor.getServicePid())) {
                    continue;
                }
                // 计算当前健康分数
                log.warn("rssMemorySize:{}", map.get(serviceMonitor.getServicePid()));
                log.warn("totalMemorySize:{}", totalMemorySize);
                int healthScore = calcHealthScore(serviceMonitor, map.get(serviceMonitor.getServicePid()), totalMemorySize);
                log.warn("当前健康分数：{}", healthScore);
                // 判断当前服务监控连续触发次数是否到达阈值，并进行后续操作
                judgeHealth(serviceMonitor, healthScore);
            }

            log.info("[服务质量监测线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.SERVICE_MONITOR_THREAD, true));

        } catch (Exception e) {
            log.error("[Service-Monitor-Error]" + e);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.SERVICE_MONITOR_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }
    }
}
