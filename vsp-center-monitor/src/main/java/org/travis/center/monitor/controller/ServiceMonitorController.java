package org.travis.center.monitor.controller;

import org.travis.center.monitor.service.ServiceMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* 服务质量监控记录表(VSP.VSP_SERVICE_MONITOR)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/service")
public class ServiceMonitorController {

    @Resource
    private ServiceMonitorService serviceMonitorService;

}
