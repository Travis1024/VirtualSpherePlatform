package org.travis.center.manage.controller;
import org.travis.center.common.entity.manage.NetworkLayerInfo;
import org.springframework.web.bind.annotation.*;

/**
* (VSP.VSP_NETWORK_LAYER_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/networkLayer")
public class NetworkLayerInfoController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public NetworkLayerInfo selectOne(Integer id) {
        return null;
    }

}
