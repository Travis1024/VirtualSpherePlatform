package org.travis.center.monitor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.monitor.ServiceMonitorMapper;
import org.travis.center.common.entity.monitor.ServiceMonitor;
import org.travis.center.monitor.service.ServiceMonitorService;
/**
 * @ClassName ServiceMonitorServiceImpl
 * @Description ServiceMonitorServiceImpl
 * @Author Travis
 * @Data 2024/10
 */
@Service
public class ServiceMonitorServiceImpl extends ServiceImpl<ServiceMonitorMapper, ServiceMonitor> implements ServiceMonitorService{

}
