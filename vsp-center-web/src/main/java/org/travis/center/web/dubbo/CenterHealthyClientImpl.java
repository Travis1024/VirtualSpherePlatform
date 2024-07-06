package org.travis.center.web.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.travis.api.client.center.CenterHealthyClient;
import org.travis.api.pojo.bo.HostHealthyStateBO;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.domain.R;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName CenterHealthyClientImpl
 * @Description CenterHealthyClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Slf4j
@DubboService
public class CenterHealthyClientImpl implements CenterHealthyClient {

    @Resource
    public RedissonClient redissonClient;

    @Override
    public R<Void> dubboHealthyCheck() {
        log.info("[center] dubbo healthy check");
        return R.ok();
    }

    @Override
    public R<Void> pushHostHealthyState(HostHealthyStateBO hostHealthyStateBO) {
        try {
            // 处理心跳推送信息
            // 1.处理宿主机
            RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet(RedissonConstant.HEALTHY_HOST_RECORDS + hostHealthyStateBO.getHostIp());
            scoredSortedSet.add(hostHealthyStateBO.getRecordTime(), String.valueOf(hostHealthyStateBO.getRecordTime()));

            // 2.处理虚拟机
            RMap<String, String> rMap = redissonClient.getMap(RedissonConstant.HEALTHY_VMWARE_RECORDS);
            Map<String, String> vmwareUuidStateMap = hostHealthyStateBO.getVmwareUuidStateMap();
            for (Map.Entry<String, String> entry : vmwareUuidStateMap.entrySet()) {
                String vmUuid = entry.getKey();
                String vmState = entry.getValue();
                rMap.put(vmUuid, vmState);
            }
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.error(e.toString());
        }
    }
}
