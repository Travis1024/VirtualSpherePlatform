package org.travis.center.manage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.api.client.host.HealthyClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.manage.pojo.dto.HostInsertDTO;
import org.travis.center.manage.service.HostInfoService;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

/**
 * @ClassName HostInfoServiceImpl
 * @Description HostInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Slf4j
@Service
public class HostInfoServiceImpl extends ServiceImpl<HostInfoMapper, HostInfo> implements HostInfoService{

    @DubboReference
    private HealthyClient healthyClient;

    @Override
    public HostInfo insertOne(HostInsertDTO hostInsertDTO) {
        // 1.PING Dubbo 请求
        try {
            R<String> healthyCheckR = healthyClient.healthyCheck(hostInsertDTO.getIp());
            if (healthyCheckR.checkFail()) {
                throw new DubboFunctionException(healthyCheckR.getMsg());
            }
        } catch (Exception e) {
            log.error("[{} - Healthy Check Error] -> {}", hostInsertDTO.getIp(), e.getMessage());
            throw new CommonException(BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getCode(), BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getMessage() + StrUtil.COLON + e.getMessage());
        }

        // 2.数据库记录存储
        HostInfo hostInfo = new HostInfo();
        BeanUtils.copyProperties(hostInsertDTO, hostInfo);
        hostInfo.setId(SnowflakeIdUtil.nextId());
        save(hostInfo);
        return hostInfo;
    }


    @Override
    public boolean checkHostSshConnect(String hostIp, Integer hostSshPort, String username, String password) {
        Session session = null;
        try {
            JSch jSch = new JSch();
            session = jSch.getSession(username, hostIp, hostSshPort);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(8000);
            session.setServerAliveInterval(5000);
            session.connect();
            return true;
        } catch (Exception exception) {
            log.error("[{}] SSH Connect Pre-Check Error: {}", hostIp, exception.getMessage());
            return false;
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
