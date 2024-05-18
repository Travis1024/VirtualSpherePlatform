package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.VspStrUtil;

import java.io.File;
import java.util.Optional;

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
        // 1.检测 IP 地址是否已经存在
        Assert.isFalse(checkHostIpUnique(hostInsertDTO.getIp()), () -> new BadRequestException("宿主机 IP 地址已存在!"));

        // 2.PING Dubbo 请求
        try {
            R<String> healthyCheckR = healthyClient.healthyCheck(hostInsertDTO.getIp());
            Assert.isFalse(healthyCheckR.checkFail(), () -> new DubboFunctionException(healthyCheckR.getMsg()));
        } catch (Exception e) {
            log.error("[{} - Healthy Check Error] -> {}", hostInsertDTO.getIp(), e.getMessage());
            throw new CommonException(BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getCode(), BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getMessage() + StrUtil.COLON + e.getMessage());
        }

        // 3.数据库记录存储
        HostInfo hostInfo = new HostInfo();
        BeanUtils.copyProperties(hostInsertDTO, hostInfo);
        hostInfo.setId(SnowflakeIdUtil.nextId());
        // 删除最后的"/"符号
        if (hostInfo.getSharedStoragePath().endsWith(File.separator)) {
            hostInfo.setSharedStoragePath(hostInfo.getSharedStoragePath().substring(0, hostInfo.getSharedStoragePath().length() - 1));
        }
        VspStrUtil.trimStr(hostInfo);
        save(hostInfo);
        return hostInfo;
    }


    /**
     * @MethodName checkHostIpUnique
     * @Description 检测当前宿主机 IP 地址数据库中是否存在
     * @Author travis-wei
     * @Data 2024/5/13
     * @param hostIp	宿主机 IP
     * @Return boolean  true-存在，false-不存在
     **/
    private boolean checkHostIpUnique(String hostIp) {
        return Optional.ofNullable(getOne(Wrappers.<HostInfo>lambdaQuery().select(HostInfo::getName).eq(HostInfo::getIp, hostIp))).isPresent();
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
