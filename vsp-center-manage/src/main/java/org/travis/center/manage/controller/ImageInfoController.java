package org.travis.center.manage.controller;
import org.travis.center.common.entity.manage.ImageInfo;
import org.springframework.web.bind.annotation.*;

/**
* (VSP.VSP_IMAGE_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/image")
public class ImageInfoController {

    /**
    * 通过主键查询单条数据
    *
    * @param id 主键
    * @return 单条数据
    */
    @GetMapping("selectOne")
    public ImageInfo selectOne(Integer id) {
        return null;
    }

}
