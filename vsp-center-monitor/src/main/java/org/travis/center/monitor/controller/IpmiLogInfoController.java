package org.travis.center.monitor.controller;

import org.travis.center.monitor.service.IpmiLogInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* Ipmi日志信息表(VSP.VSP_IPMI_LOG_INFO)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/ipmi")
public class IpmiLogInfoController {

    @Resource
    private IpmiLogInfoService ipmiLogInfoService;

}
