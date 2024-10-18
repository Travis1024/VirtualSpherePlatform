package org.travis.center.monitor.threads.addition;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.platform.common.constants.StatisticConstant;
import com.hust.platform.common.entity.IpmiLogInfo;
import com.hust.platform.common.mapper.IpmiLogInfoMapper;
import com.hust.platform.common.utils.ApplicationContextUtil;
import com.hust.platform.logger.service.LogInfoService;
import com.hust.platform.logger.threads.TaskThreadNumberStatistic;
import com.hust.platform.monitor.service.IpmiLogInfoService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadIpmiSelMonitor
 * @Description ipmi 日志数据监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Slf4j
public class ThreadIpmiLogMonitor implements Runnable{

    private String uuid;
    private String jsonStr;
    private LogInfoService logInfoService;
    private ThreadPoolExecutor threadPoolExecutor;
    private IpmiLogInfoService ipmiLogInfoService;
    private IpmiLogInfoMapper ipmiLogInfoMapper;

    public ThreadIpmiLogMonitor(String uuid, String jsonStr) {
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.logInfoService = ApplicationContextUtil.getBean(LogInfoService.class);
        this.threadPoolExecutor = ApplicationContextUtil.getBean(ThreadPoolExecutor.class);
        this.ipmiLogInfoService = ApplicationContextUtil.getBean(IpmiLogInfoService.class);
        this.ipmiLogInfoMapper = ApplicationContextUtil.getBean(IpmiLogInfoMapper.class);
    }


    @Override
    public void run() {
        try {
            // 0、初始化列表
            List<IpmiLogInfo> resultList = new ArrayList<>();

            // 1、将 json 字符串转为 JsonNode 节点
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNodeArray = objectMapper.readTree(jsonStr);

            // 2、遍历节点数组
            for (JsonNode rootNode : rootNodeArray) {
                // 2.1 获取节点信息
                String name = rootNode.get("name").asText();
                String ip = rootNode.get("ip").asText();
                String port = rootNode.get("port").asText();
                long timestamp = rootNode.get("timestamp").asLong();

                // 2.2 查询是否存在记录自增ID
                IpmiLogInfo selectOne = ipmiLogInfoMapper.selectOne(
                        Wrappers.<IpmiLogInfo>lambdaQuery()
                                .eq(IpmiLogInfo::getIpmiLogName, name)
                                .eq(IpmiLogInfo::getIpmiLogIp, ip)
                                .eq(IpmiLogInfo::getIpmiLogPort, port)
                                .select(IpmiLogInfo::getIpmiLogZzid)
                );

                // 2.3 组装成存储数据
                IpmiLogInfo ipmiLogInfo = new IpmiLogInfo()
                        .setIpmiLogName(name)
                        .setIpmiLogIp(ip)
                        .setIpmiLogPort(port)
                        .setIpmiLogTimestamp(timestamp)
                        .setIpmiLogData(rootNode.toString());

                // 2.4 如果存在记录，则设置自增 ID
                if (selectOne != null && ObjectUtil.isNotEmpty(selectOne.getIpmiLogZzid())) {
                    ipmiLogInfo.setIpmiLogZzid(selectOne.getIpmiLogZzid());
                }

                resultList.add(ipmiLogInfo);
            }

            // 批量存储或更新
            ipmiLogInfoService.saveOrUpdateBatch(resultList, resultList.size());

            log.info("[IPMI-Sel 监测线程执行结束] -> " + uuid);
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.IPMI_LOG_THREAD, true));
        } catch (Exception e) {
            log.error(e.toString());
            threadPoolExecutor.execute(new TaskThreadNumberStatistic(StatisticConstant.IPMI_LOG_THREAD, false));
            StackTraceElement traceElement = e.getStackTrace()[0];
            logInfoService.saveThreadExceptionLog(traceElement.getClassName(), traceElement.getMethodName(), e.toString(), DateUtil.date());
        }
    }
}
