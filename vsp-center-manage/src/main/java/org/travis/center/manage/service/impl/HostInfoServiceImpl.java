package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.api.client.agent.AgentHealthyClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.service.AgentAssistService;
import org.travis.center.manage.pojo.dto.HostInsertDTO;
import org.travis.center.manage.pojo.dto.HostUpdateDTO;
import org.travis.center.manage.service.HostInfoService;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.DubboFunctionException;
import org.travis.shared.common.utils.SnowflakeIdUtil;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
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
    private AgentHealthyClient agentHealthyClient;
    @Resource
    private AgentAssistService agentAssistService;

    @Override
    public HostInfo insertOne(HostInsertDTO hostInsertDTO) {
        // 1.校验宿主机 IP
        validateHostIp(hostInsertDTO.getIp());
        // 2.数据库记录存储
        HostInfo hostInfo = new HostInfo();
        BeanUtils.copyProperties(hostInsertDTO, hostInfo);
        hostInfo.setId(SnowflakeIdUtil.nextId());
        // 3.宿主机共享存储路径赋值
        hostInfo.setSharedStoragePath(agentAssistService.getHostSharedStoragePath());

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

    /**
     * @MethodName validateHostIp
     * @Description 校验宿主机 IP 地址是否有效及 IP-Agent 健康状态
     * @Author travis-wei
     * @Data 2024/5/22
     * @param hostIp    宿主机 IP
     * @Return void
     **/
    private void validateHostIp(String hostIp) {
        // 1.校验 IP 地址格式
        Assert.isTrue(Validator.isIpv4(hostIp), () -> new BadRequestException("IPv4 地址格式非法!"));

        // 2.检测 IP 地址是否已经存在
        Assert.isFalse(checkHostIpUnique(hostIp), () -> new BadRequestException("宿主机 IP 地址已存在!"));

        // 3.PING Dubbo 请求
        try {
            R<String> healthyCheckR = agentHealthyClient.healthyCheck(hostIp);
            Assert.isFalse(healthyCheckR.checkFail(), () -> new DubboFunctionException(healthyCheckR.getMsg()));
        } catch (Exception e) {
            log.error("[HostInfoServiceImpl::insertOne] {} - Agent Healthy Check Error! -> {}", hostIp, e.getMessage());
            throw new CommonException(BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getCode(), BizCodeEnum.DUBBO_HEALTHY_CHECK_ERROR.getMessage() + StrUtil.COLON + e.getMessage());
        }
    }

    @Override
    public boolean validateHostSshConnect(String hostIp, Integer hostSshPort, String username, String password) {
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
            log.error("[HostInfoServiceImpl::checkHostSshConnect] {} SSH Connect Pre-Check Error: {}", hostIp, exception.getMessage());
            return false;
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Transactional
    @Override
    public void delete(List<Long> hostIdList) {
        removeBatchByIds(hostIdList);
    }

    @Override
    public void updateOne(HostUpdateDTO hostUpdateDTO) {
        HostInfo hostInfo = new HostInfo();
        BeanUtils.copyProperties(hostUpdateDTO, hostInfo);
        updateById(hostInfo);
    }

    @Override
    public void updateHostIp(Long hostId, String hostIp) {
        // 1.校验宿主机 IP 地址
        validateHostIp(hostIp);
        // 2.更新 IP 地址
        update(
                Wrappers.<HostInfo>lambdaUpdate()
                        .set(HostInfo::getIp, hostIp)
                        .eq(HostInfo::getId, hostId)
        );
    }

    @Override
    public boolean validateHostAgentConnect(String ipAddr) {
        validateHostIp(ipAddr);
        return true;
    }

    @Override
    public PageResult<HostInfo> pageSelectList(PageQuery pageQuery) {
        Page<HostInfo> hostInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(hostInfoPage);
    }

    @Override
    public List<HostInfo> selectList() {
        return getBaseMapper().selectList(null);
    }
}
