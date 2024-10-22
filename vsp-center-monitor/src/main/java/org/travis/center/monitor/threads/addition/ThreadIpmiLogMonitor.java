package org.travis.center.monitor.threads.addition;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.travis.center.common.entity.monitor.IpmiLogInfo;
import org.travis.center.common.mapper.monitor.IpmiLogInfoMapper;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.center.monitor.service.IpmiLogInfoService;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ThreadIpmiSelMonitor
 * @Description ipmi 日志数据监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Slf4j
public class ThreadIpmiLogMonitor implements Runnable{

    private final String uuid;
    private final String jsonStr;
    private final IpmiLogInfoService ipmiLogInfoService;
    private final IpmiLogInfoMapper ipmiLogInfoMapper;

    public ThreadIpmiLogMonitor(String uuid, String jsonStr) {
        this.uuid = uuid;
        this.jsonStr = jsonStr;
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

                // 2.2 查询是否存在记录
                IpmiLogInfo selectOne = ipmiLogInfoMapper.selectOne(
                        Wrappers.<IpmiLogInfo>lambdaQuery()
                                .eq(IpmiLogInfo::getIpmiName, name)
                                .eq(IpmiLogInfo::getIpmiIp, ip)
                                .eq(IpmiLogInfo::getIpmiPort, port)
                                .select(IpmiLogInfo::getId)
                );

                // 2.3 组装成存储数据
                IpmiLogInfo ipmiLogInfo = new IpmiLogInfo()
                        .setIpmiName(name)
                        .setIpmiIp(ip)
                        .setIpmiPort(port)
                        .setIpmiTimestamp(timestamp)
                        .setIpmiData(rootNode.toString());

                // 2.4 如果存在记录，则设置自增 ID
                if (selectOne != null && ObjectUtil.isNotEmpty(selectOne.getId())) {
                    ipmiLogInfo.setId(selectOne.getId());
                } else {
                    ipmiLogInfo.setId(SnowflakeIdUtil.nextId());
                }

                resultList.add(ipmiLogInfo);
            }

            // 批量存储或更新
            ipmiLogInfoService.saveOrUpdateBatch(resultList, resultList.size());

            log.info("[IPMI-Sel 监测线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[IPMI-SEL-ERROR-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
