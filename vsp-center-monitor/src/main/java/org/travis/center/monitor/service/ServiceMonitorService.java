package org.travis.center.monitor.service;

import org.travis.center.common.entity.monitor.ServiceMonitor;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.monitor.pojo.dto.AddServiceMonitorDTO;
import org.travis.center.monitor.pojo.dto.ManualServiceReplaceDTO;
import org.travis.center.monitor.pojo.dto.UpdateServiceMonitorDTO;
import org.travis.center.monitor.pojo.vo.QueryServiceListVO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.MachineTypeEnum;

import java.util.List;

/**
 * @ClassName ServiceMonitorService
 * @Description ServiceMonitorService
 * @Author Travis
 * @Data 2024/10
 */
public interface ServiceMonitorService extends IService<ServiceMonitor> {

    List<ServiceMonitor> queryInfoList();

    PageResult<ServiceMonitor> pageQueryInfoList(PageQuery pageQuery);

    List<QueryServiceListVO> queryRunningServiceList(MachineTypeEnum machineType, String machineUuid);

    List<QueryServiceListVO> queryAllStateServiceList(MachineTypeEnum machineType, String machineUuid);

    ServiceMonitor addServiceMonitor(AddServiceMonitorDTO addServiceMonitorDTO);

    void startServiceMonitor(Long id);

    void stopServiceMonitor(Long id);

    void deleteServiceMonitor(Long id);

    void updateServiceMonitor(UpdateServiceMonitorDTO updateServiceMonitorDTO);

    void manualReplaceService(ManualServiceReplaceDTO manualServiceReplaceDTO);

    R<?> execReplace(String machineUuid, ServiceMonitor serviceMonitor, Integer currentHealthScore);
}
