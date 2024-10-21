package org.travis.center.common.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.monitor.ServiceMonitor;

/**
 * @ClassName ServiceMonitorMapper
 * @Description ServiceMonitorMapper
 * @Author Travis
 * @Data 2024/10
 */
@Mapper
public interface ServiceMonitorMapper extends BaseMapper<ServiceMonitor> {
}