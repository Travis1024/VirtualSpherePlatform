package org.travis.center.monitor.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Throwables;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.api.client.center.CenterVmwareClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.center.common.enums.HostStateEnum;
import org.travis.center.common.enums.VmwareStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.VmwareInfoMapper;
import org.travis.center.common.mapper.monitor.ServiceMonitorMapper;
import org.travis.center.common.entity.monitor.ServiceMonitor;
import org.travis.center.common.utils.RemoteConnectUtil;
import org.travis.center.monitor.pojo.dto.AddServiceMonitorDTO;
import org.travis.center.monitor.pojo.dto.ManualServiceReplaceDTO;
import org.travis.center.monitor.pojo.dto.UpdateServiceMonitorDTO;
import org.travis.center.monitor.pojo.vo.QueryServiceListVO;
import org.travis.center.monitor.service.ServiceMonitorService;
import org.travis.center.support.websocket.WsMessageHolder;
import org.travis.shared.common.constants.MonitorConstant;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.domain.WebSocketMessage;
import org.travis.shared.common.enums.*;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;

/**
 * @ClassName ServiceMonitorServiceImpl
 * @Description ServiceMonitorServiceImpl
 * @Author Travis
 * @Data 2024/10
 */
@Slf4j
@Service
public class ServiceMonitorServiceImpl extends ServiceImpl<ServiceMonitorMapper, ServiceMonitor> implements ServiceMonitorService{

    @Resource
    private HostInfoMapper hostInfoMapper;
    @Resource
    private VmwareInfoMapper vmwareInfoMapper;
    @Resource
    private CenterVmwareClient centerVmwareClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    public WsMessageHolder wsMessageHolder;

    @Override
    public List<ServiceMonitor> queryInfoList() {
        return Optional.ofNullable(getBaseMapper().selectList(null)).orElse(new ArrayList<>());
    }

    @Override
    public PageResult<ServiceMonitor> pageQueryInfoList(PageQuery pageQuery) {
        Page<ServiceMonitor> serviceMonitorPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(serviceMonitorPage);
    }

    @Override
    public List<QueryServiceListVO> queryRunningServiceList(MachineTypeEnum machineType, String machineUuid) {
        // 校验 + 获取 ssh 连接信息
        R<SshInfo> validateAndQuerySshInfoR = validateAndQuerySshInfo(machineType, machineUuid);
        if (validateAndQuerySshInfoR.checkFail()) {
            throw new CommonException(validateAndQuerySshInfoR.getCode(), validateAndQuerySshInfoR.getMsg());
        }
        SshInfo sshInfo = validateAndQuerySshInfoR.getData();

        // 执行远程查询命令
        R<?> queryRunningR = execQueryServiceList(sshInfo.getIp(), sshInfo.getUsername(), sshInfo.getPassword(), "running");
        if (queryRunningR.checkFail()) {
            throw new CommonException(queryRunningR.getCode(), queryRunningR.getMsg());
        }
        return (List<QueryServiceListVO>) queryRunningR.getData();
    }

    @Override
    public List<QueryServiceListVO> queryAllStateServiceList(MachineTypeEnum machineType, String machineUuid) {
        // 校验 + 获取 ssh 连接信息
        R<SshInfo> validateAndQuerySshInfoR = validateAndQuerySshInfo(machineType, machineUuid);
        if (validateAndQuerySshInfoR.checkFail()) {
            throw new CommonException(validateAndQuerySshInfoR.getCode(), validateAndQuerySshInfoR.getMsg());
        }
        SshInfo sshInfo = validateAndQuerySshInfoR.getData();

        // 执行远程查询命令
        R<?> queryRunningR = execQueryServiceList(sshInfo.getIp(), sshInfo.getUsername(), sshInfo.getPassword(), null);
        if (queryRunningR.checkFail()) {
            throw new CommonException(queryRunningR.getCode(), queryRunningR.getMsg());
        }
        return (List<QueryServiceListVO>) queryRunningR.getData();
    }

    @Override
    public ServiceMonitor addServiceMonitor(AddServiceMonitorDTO addServiceMonitorDTO) {
        ServiceMonitor serviceMonitor = new ServiceMonitor();
        BeanUtils.copyProperties(addServiceMonitorDTO, serviceMonitor);
        serviceMonitor.setId(SnowflakeIdUtil.nextId());
        serviceMonitor.setServiceState(ServiceStateEnum.DISABLE);

        getBaseMapper().insert(serviceMonitor);
        return serviceMonitor;
    }

    @Override
    public void startServiceMonitor(Long id) {
        // 1.查询服务信息
        ServiceMonitor serviceMonitor = Optional.ofNullable(getBaseMapper().selectById(id)).orElseThrow(() -> new CommonException(BizCodeEnum.NOT_FOUND.getCode(), "未查询到服务监控信息！"));
        if (ServiceStateEnum.ENABLE.equals(serviceMonitor.getServiceState())) {
            log.info("[服务监控] 当前服务监控正在进行中！ -> {}", serviceMonitor.getServiceName());
            return;
        }

        // 2.查询 ssh 信息
        R<SshInfo> sshInfoR = validateAndQuerySshInfo(serviceMonitor.getServiceMachineType(), serviceMonitor.getServiceMachineUuid());
        if (sshInfoR.checkFail()) {
            throw new CommonException(sshInfoR.getCode(), sshInfoR.getMsg());
        }
        SshInfo sshInfo = sshInfoR.getData();

        // 3.1.执行远程查询命令，检查需要监测的线程是否处于 running 状态
        R<?> execQueryRunningCommand = execQueryServiceList(sshInfo.getIp(), sshInfo.getUsername(), sshInfo.getPassword(), "running");
        if (execQueryRunningCommand.checkFail()) {
            throw new CommonException(execQueryRunningCommand.getCode(), execQueryRunningCommand.getMsg());
        }
        List<QueryServiceListVO> runningServiceList = (List<QueryServiceListVO>) execQueryRunningCommand.getData();
        Map<String, QueryServiceListVO> collect = runningServiceList.stream().collect(Collectors.toMap(QueryServiceListVO::getServiceName, one -> one));

        // 3.2.服务未运行则执行启动服务命令
        if (!collect.containsKey(serviceMonitor.getServiceName())) {
            R<?> execStartCommand = RemoteConnectUtil.execCommand(sshInfo.getIp(), "22", sshInfo.getUsername(), sshInfo.getPassword(), "systemctl restart " + serviceMonitor.getServiceName());
            if (execStartCommand.checkFail()) {
                throw new CommonException(execStartCommand.getCode(), execStartCommand.getMsg());
            }
        }

        // 4.查询服务进程 PID
        R<?> execQueryServicePidR = execQueryServicePid(sshInfo, serviceMonitor.getServiceName());
        if (execQueryServicePidR.checkFail()) {
            throw new CommonException(execQueryServicePidR.getCode(), execQueryServicePidR.getMsg());
        }
        serviceMonitor.setServicePid(Integer.valueOf(execQueryServicePidR.getData().toString()));
        serviceMonitor.setServiceState(ServiceStateEnum.ENABLE);

        // 5.1.更新服务监控信息
        getBaseMapper().updateById(serviceMonitor);
        // 5.2.清空非健康状态计数缓存
        stringRedisTemplate.delete(MonitorConstant.SERVICE_SUM_PREFIX + StrUtil.COLON + serviceMonitor.getServiceMachineUuid() + StrUtil.COLON + serviceMonitor.getServiceName());
        // 5.3.加入监控缓存
        stringRedisTemplate.opsForValue().set(MonitorConstant.SERVICE_PREFIX + StrUtil.COLON + serviceMonitor.getServiceMachineUuid() + StrUtil.COLON + serviceMonitor.getServiceName(), JSONUtil.toJsonStr(serviceMonitor));
    }

    @Override
    public void stopServiceMonitor(Long id) {
        // 1.查询服务信息
        ServiceMonitor serviceMonitor = Optional.ofNullable(getBaseMapper().selectById(id)).orElseThrow(() -> new CommonException(BizCodeEnum.NOT_FOUND.getCode(), "未查询到服务监控信息！"));
        // 2.更新数据库状态信息
        getBaseMapper().update(
                Wrappers.<ServiceMonitor>lambdaUpdate()
                        .set(ServiceMonitor::getServiceState, ServiceStateEnum.DISABLE)
                        .eq(ServiceMonitor::getId, id)
        );
        // 3.删除监控缓存
        stringRedisTemplate.delete(MonitorConstant.SERVICE_PREFIX + StrUtil.COLON + serviceMonitor.getServiceMachineUuid() + StrUtil.COLON + serviceMonitor.getServiceName());
        // 4.清空非健康状态计数缓存
        stringRedisTemplate.delete(MonitorConstant.SERVICE_SUM_PREFIX + StrUtil.COLON + serviceMonitor.getServiceMachineUuid() + StrUtil.COLON + serviceMonitor.getServiceName());
    }

    @Override
    public void deleteServiceMonitor(Long id) {
        // 1.查询服务信息
        ServiceMonitor serviceMonitor = Optional.ofNullable(getBaseMapper().selectById(id)).orElseThrow(() -> new CommonException(BizCodeEnum.NOT_FOUND.getCode(), "未查询到服务监控信息！"));
        // 2.停止服务监控信息
        if (ServiceStateEnum.ENABLE.equals(serviceMonitor.getServiceState())) {
            stopServiceMonitor(id);
        }
        // 3.删除服务监控信息
        getBaseMapper().deleteById(id);
    }

    @Override
    public void updateServiceMonitor(UpdateServiceMonitorDTO updateServiceMonitorDTO) {
        ServiceMonitor serviceMonitor = getBaseMapper().selectById(updateServiceMonitorDTO.getId());
        if (serviceMonitor == null) {
            throw new CommonException(BizCodeEnum.NOT_FOUND.getCode(), "未查询到服务监控信息！");
        }

        UpdateWrapper<ServiceMonitor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ServiceMonitor.COL_ID, updateServiceMonitorDTO.getId());

        if (StrUtil.isNotEmpty(updateServiceMonitorDTO.getServiceReplaceName())) {
            updateWrapper.set(ServiceMonitor.COL_SERVICE_REPLACE_NAME, updateServiceMonitorDTO.getServiceReplaceName());
        }
        if (updateServiceMonitorDTO.getServiceCpuLimitRate() != null) {
            updateWrapper.set(ServiceMonitor.COL_SERVICE_CPU_LIMIT_RATE, updateServiceMonitorDTO.getServiceCpuLimitRate());
        }
        if (updateServiceMonitorDTO.getServiceMemLimitRate() != null) {
            updateWrapper.set(ServiceMonitor.COL_SERVICE_MEM_LIMIT_RATE, updateServiceMonitorDTO.getServiceMemLimitRate());
        }
        if (updateServiceMonitorDTO.getServiceHealthLimitScore() != null) {
            updateWrapper.set(ServiceMonitor.COL_SERVICE_HEALTH_LIMIT_SCORE, updateServiceMonitorDTO.getServiceHealthLimitScore());
        }
        if (updateServiceMonitorDTO.getServiceAutoType() != null) {
            updateWrapper.set(ServiceMonitor.COL_SERVICE_AUTO_TYPE, updateServiceMonitorDTO.getServiceAutoType());
        }
        if (updateServiceMonitorDTO.getServicePid() != null) {
            updateWrapper.set(ServiceMonitor.COL_SERVICE_PID, updateServiceMonitorDTO.getServicePid());
        }
        // 更新数据库信息
        getBaseMapper().update(null, updateWrapper);

        // 查询数据库信息
        serviceMonitor = getBaseMapper().selectById(updateServiceMonitorDTO.getId());
        if (serviceMonitor == null) {
            throw new CommonException(BizCodeEnum.NOT_FOUND.getCode(), "未查询到服务监控信息！");
        }

        if (ServiceStateEnum.ENABLE.equals(serviceMonitor.getServiceState())) {
            // 更新监控缓存
            stringRedisTemplate.opsForValue().set(MonitorConstant.SERVICE_PREFIX + StrUtil.COLON + serviceMonitor.getServiceMachineUuid() + StrUtil.COLON + serviceMonitor.getServiceName(), JSONUtil.toJsonStr(serviceMonitor));
        }
    }

    @Override
    public void manualReplaceService(ManualServiceReplaceDTO manualServiceReplaceDTO) {
        // 1.校验并获取 ssh 连接信息
        R<SshInfo> sshInfoR = validateAndQuerySshInfo(manualServiceReplaceDTO.getServiceMachineType(), manualServiceReplaceDTO.getServiceMachineUuid());
        if (sshInfoR.checkFail()) {
            throw new CommonException(sshInfoR.getCode(), sshInfoR.getMsg());
        }
        SshInfo sshInfo = sshInfoR.getData();

        // 2.执行服务停止命令
        R<?> execStopCommand = RemoteConnectUtil.execCommand(sshInfo.getIp(), "22", sshInfo.getUsername(), sshInfo.getPassword(), "systemctl stop " + manualServiceReplaceDTO.getServiceName());
        if (execStopCommand.checkFail()) {
            throw new CommonException(execStopCommand.getCode(), execStopCommand.getMsg());
        }

        // 3.执行服务启动命令
        R<?> execStartCommand = RemoteConnectUtil.execCommand(sshInfo.getIp(), "22", sshInfo.getUsername(), sshInfo.getPassword(), "systemctl restart " + manualServiceReplaceDTO.getServiceReplaceName());
        if (execStartCommand.checkFail()) {
            throw new CommonException(execStartCommand.getCode(), execStartCommand.getMsg());
        }
    }

    @Override
    public R<?> execReplace(String machineUuid, ServiceMonitor serviceMonitor, Integer currentHealthScore) {
        if (ServiceControlTypeEnum.SEMI_AUTOMATIC.equals(serviceMonitor.getServiceAutoType())) {
            // 半自动: 执行替换推送
            wsMessageHolder.sendGlobalMessage(
                    WebSocketMessage.builder()
                            .msgTitle("服务半自动调控")
                            .msgModule(MsgModuleEnum.SERVICE)
                            .msgState(MsgStateEnum.ALARM)
                            .msgContent("当前健康分数：" + currentHealthScore + " | " + serviceMonitor)
                            .nodeMachineType(serviceMonitor.getServiceMachineType())
                            .nodeMachineUuid(machineUuid)
                            .build()
            );
        } else if (ServiceControlTypeEnum.AUTOMATIC.equals(serviceMonitor.getServiceAutoType())) {
            // 自动: 执行服务自动替换
            R<?> execAutoReplace = execAutoReplace(serviceMonitor);
            if (execAutoReplace.checkFail()) {
                return execAutoReplace;
            }
            wsMessageHolder.sendGlobalMessage(
                    WebSocketMessage.builder()
                            .msgTitle("服务自动调控")
                            .msgModule(MsgModuleEnum.SERVICE)
                            .msgState(MsgStateEnum.INFO)
                            .msgContent("当前健康分数：" + currentHealthScore + " | " + serviceMonitor)
                            .nodeMachineType(serviceMonitor.getServiceMachineType())
                            .nodeMachineUuid(machineUuid)
                            .build()
            );
        }
        return R.ok();
    }

    @Data
    @Accessors(chain = true)
    static class SshInfo {
        private String ip;
        private String username;
        private String password;
    }

    /**
     * 校验并获取 ssh 连接信息
     *
     * @param machineType 节点类型
     * @param machineUuid 节点UUID
     * @return ssh连接信息
     */
    private R<SshInfo> validateAndQuerySshInfo(MachineTypeEnum machineType, String machineUuid) {
        try {
            if (machineType == null || StrUtil.isEmpty(machineUuid)) {
                throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "节点类型或节点UUID不能为空！");
            }

            SshInfo sshInfo = new SshInfo();

            if (MachineTypeEnum.HOST.equals(machineType)) {
                HostInfo hostInfo = Optional.ofNullable(hostInfoMapper.selectOne(Wrappers.<HostInfo>lambdaQuery().eq(HostInfo::getUuid, machineUuid)))
                        .orElseThrow(() -> new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "宿主机不存在！"));

                if (hostInfo.getState() == null || !hostInfo.getState().equals(HostStateEnum.READY)) {
                    throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "宿主机状态异常，请检查宿主机状态！");
                }
                if (StrUtil.isEmpty(hostInfo.getIp())) {
                    throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "宿主机IP为空，请检查宿主机配置！");
                }
                if (StrUtil.isEmpty(hostInfo.getLoginUser()) || StrUtil.isEmpty(hostInfo.getLoginPassword())) {
                    throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "宿主机登录用户或密码为空，请检查宿主机配置！");
                }
                sshInfo.setIp(hostInfo.getIp()).setUsername(hostInfo.getLoginUser()).setPassword(hostInfo.getLoginPassword());

            } else if (MachineTypeEnum.VMWARE.equals(machineType)) {
                VmwareInfo vmwareInfo = Optional.ofNullable(vmwareInfoMapper.selectOne(Wrappers.<VmwareInfo>lambdaQuery().eq(VmwareInfo::getUuid, machineUuid)))
                        .orElseThrow(() -> new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "虚拟机不存在！"));

                if (vmwareInfo.getState() == null || !vmwareInfo.getState().equals(VmwareStateEnum.RUNNING)) {
                    throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "虚拟机处于非运行状态，请确保虚拟机运行正常！");
                }
                if (StrUtil.isEmpty(vmwareInfo.getLoginUsername()) || StrUtil.isEmpty(vmwareInfo.getLoginPassword())) {
                    throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "虚拟机登录用户或密码为空，请检查虚拟机配置！");
                }
                R<String> ipAddrR = centerVmwareClient.queryIpAddr(vmwareInfo.getId());
                if (ipAddrR.checkFail()) {
                    throw new CommonException(BizCodeEnum.BAD_REQUEST.getCode(), "虚拟机IP地址查询失败！" + ipAddrR.getMsg());
                }
                sshInfo.setIp(ipAddrR.getData()).setUsername(vmwareInfo.getLoginUsername()).setPassword(vmwareInfo.getLoginPassword());
            }

            return R.ok(sshInfo);

        } catch (CommonException e) {
            log.error("[ServiceMonitorServiceImpl::validateAndQuerySshInfo] -> {}", e.toString());
            return R.error(e.getCode(), e.getMessage());
        }
    }

    /**
     * 执行自动替换服务
     *
     * @param serviceMonitor 监控对象
     * @return R
     */
    private R<?> execAutoReplace(ServiceMonitor serviceMonitor) {
        // 1.获取 ssh 信息
        R<SshInfo> validateAndQuerySshInfo = validateAndQuerySshInfo(serviceMonitor.getServiceMachineType(), serviceMonitor.getServiceMachineUuid());
        if (validateAndQuerySshInfo.checkFail()) {
            return validateAndQuerySshInfo;
        }
        SshInfo sshInfo = validateAndQuerySshInfo.getData();

        // 2.执行服务停止命令
        R<?> stopExecked = RemoteConnectUtil.execCommand(sshInfo.getIp(), "22", sshInfo.getUsername(), sshInfo.getPassword(), "systemctl stop " + serviceMonitor.getServiceName());
        if (stopExecked.checkFail()) {
            return stopExecked;
        }

        // 3.执行替换服务启动命令
        R<?> startExecked = RemoteConnectUtil.execCommand(sshInfo.getIp(), "22", sshInfo.getUsername(), sshInfo.getPassword(), "systemctl restart " + serviceMonitor.getServiceReplaceName());
        if (startExecked.checkFail()) {
            return stopExecked;
        }

        // 4.结束旧进程监测
        stopServiceMonitor(serviceMonitor.getId());
        return R.ok();
    }

    /**
     * 查询服务进程号
     *
     * @param sshInfo ssh连接信息
     * @param serviceName 服务名称
     * @return org.travis.shared.common.domain.R<?>
     **/
    private R<?> execQueryServicePid(SshInfo sshInfo, String serviceName) {
        // 1. 查询服务状态
        R<?> execkedCommand = RemoteConnectUtil.execCommand(sshInfo.getIp(), "22", sshInfo.getUsername(), sshInfo.getPassword(), "systemctl status " + serviceName);
        if (execkedCommand.checkFail()) {
            return execkedCommand;
        }
        String resultStr = execkedCommand.getData().toString().trim();
        if (StrUtil.isEmpty(resultStr)) {
            return R.error(BizCodeEnum.BAD_REQUEST.getCode(), "进程号查询失败，请检查进程名称或进程状态！");
        }
        String[] lineArray = resultStr.split("\n");

        // 2. 解析进程号
        for (String line : lineArray) {
            line = line.trim();
            if (line.contains("Main PID")) {
                String pid = line.substring("Main PID:".length(), line.lastIndexOf('(')).trim();
                return StrUtil.isEmpty(pid) ? R.error(BizCodeEnum.BAD_REQUEST.getCode(), "进程号查询失败，请检查进程名称或进程状态！") : R.ok(pid);
            }
        }
        return R.error(BizCodeEnum.BAD_REQUEST.getCode(), "进程号查询失败，请检查进程名称或进程状态！");
    }

    /**
     * 查询服务列表
     *
     * @param ip 目标IP
     * @param loginUser 登录用户
     * @param loginPassword 登录密码
     * @param state 目标状态
     * @return R<List<QueryServiceListVO>>
     */
    private R<?> execQueryServiceList(String ip, String loginUser, String loginPassword, String state) {
        try {
            // 刷新服务
            RemoteConnectUtil.execCommand(ip, "22", loginUser, loginPassword, "systemctl daemon-reload");

            List<QueryServiceListVO> resultList = new ArrayList<>();

            if (state == null) {
                R<?> execkedCommand = RemoteConnectUtil.execCommand(ip, "22", loginUser, loginPassword, "systemctl list-unit-files --type=service");
                if (execkedCommand.checkFail()) {
                    return execkedCommand;
                }
                String resultStr = execkedCommand.getData().toString();
                String[] splitArray = resultStr.split("\n");

                for (String line : splitArray) {
                    line = line.trim();
                    if (line.contains(".service")) {
                        QueryServiceListVO serviceListVO = new QueryServiceListVO();
                        // 提取 serviceName
                        int firstIndex = line.indexOf(".service");
                        String serviceName = line.substring(0, firstIndex + ".service".length()).trim();

                        serviceListVO.setServiceName(serviceName);
                        resultList.add(serviceListVO);
                    }
                }

            } else {
                R<?> execkedCommand = RemoteConnectUtil.execCommand(ip, "22", loginUser, loginPassword, "systemctl list-units --type=service --state=" + state);
                if (execkedCommand.checkFail()) {
                    return execkedCommand;
                }

                String resultStr = execkedCommand.getData().toString();
                String[] splitArray = resultStr.split("\n");

                for (String line : splitArray) {
                    line = line.trim();
                    if (line.contains(".service")) {
                        QueryServiceListVO serviceListVO = new QueryServiceListVO();
                        // 提取 serviceName
                        int firstIndex = line.indexOf(".service");
                        String serviceName = line.substring(0, firstIndex + ".service".length()).trim();

                        // 截取当前行（除去 serviceName）
                        line = line.substring(firstIndex + ".service".length()).trim();
                        String[] split = line.split("\\s+");

                        StringBuilder stringBuilder = new StringBuilder();

                        if (split.length > 3) {
                            for (int i = 3; i < split.length; i++) {
                                stringBuilder.append(split[i]).append(" ");
                            }
                        }

                        serviceListVO.setServiceName(serviceName);
                        serviceListVO.setServiceLoad(split[0]);
                        serviceListVO.setServiceActive(split[1]);
                        serviceListVO.setServiceSub(split[2]);
                        serviceListVO.setServiceDescription(stringBuilder.toString());
                        resultList.add(serviceListVO);
                    }
                }
            }

            return R.ok(resultList);
        } catch (Exception e) {
            log.error("[Query-Service-List] Error:{}, IP:{}", Throwables.getStackTraceAsString(e), ip);
            return R.error(BizCodeEnum.INTERNAL_SERVER_ERROR.getCode(), Throwables.getStackTraceAsString(e));
        }
    }
}
