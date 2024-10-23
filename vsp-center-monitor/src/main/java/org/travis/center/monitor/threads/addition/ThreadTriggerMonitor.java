package org.travis.center.monitor.threads.addition;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.utils.ApplicationContextUtil;
import org.travis.center.support.pojo.vo.TriggerInfoVO;
import org.travis.center.support.websocket.WsMessageHolder;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.shared.common.enums.MachineTypeEnum;
import org.travis.shared.common.enums.MsgModuleEnum;
import org.travis.shared.common.enums.MsgStateEnum;

/**
 * @ClassName TaskTriggerMonitorThread
 * @Description 报警触发事件监控线程
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/9/20
 */
@Slf4j
public class ThreadTriggerMonitor implements Runnable{

    private final String uuid;
    private final String jsonStr;
    private final WsMessageHolder wsMessageHolder;
    private final VmwareInfoMapper vmwareInfoMapper;

    public ThreadTriggerMonitor(String uuid, String jsonStr, Long alarmIntervalTime) {
        this.uuid = uuid;
        this.jsonStr = jsonStr;
        this.wsMessageHolder = ApplicationContextUtil.getBean(WsMessageHolder.class);
        this.vmwareInfoMapper = ApplicationContextUtil.getBean(VmwareInfoMapper.class);
    }

    @Override
    public void run() {
        try {
            // 1.解析 json 数据
            TriggerInfoVO triggerInfoVO = JSONUtil.toBean(jsonStr, TriggerInfoVO.class);

            // 2.获取节点类型
            MachineTypeEnum nodeMachineType;
            VmwareInfo vmwareInfo = vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, uuid));
            if (vmwareInfo != null) {
                nodeMachineType = MachineTypeEnum.VMWARE;
            } else {
                nodeMachineType = MachineTypeEnum.HOST;
            }

            // 3.全局推送报警信息
            wsMessageHolder.sendGlobalMessage(
                    WebSocketMessage.builder()
                            .msgModule(MsgModuleEnum.MONITOR)
                            .msgTitle("Trigger报警事件")
                            .msgState(MsgStateEnum.ALARM)
                            .msgContent(triggerInfoVO.getKey() + " | " + triggerInfoVO.getValue())
                            .nodeMachineType(nodeMachineType)
                            .nodeMachineUuid(uuid)
                            .build()
            );

            log.info("[Trigger 监测线程执行结束] -> {}", uuid);
        } catch (Exception e) {
            log.error("[Trigger-ERROR-{}] {}", uuid, e.getMessage());
            log.error(e.toString());
        }
    }
}
