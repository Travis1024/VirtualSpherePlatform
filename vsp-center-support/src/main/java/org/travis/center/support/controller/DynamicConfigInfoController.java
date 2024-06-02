package org.travis.center.support.controller;

import org.travis.center.support.service.DynamicConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
* (VSP.VSP_DYNAMIC_CONFIG_INFO)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/dynamic")
public class DynamicConfigInfoController {

    @Resource
    private DynamicConfigInfoService dynamicConfigInfoService;

}
