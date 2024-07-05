package org.travis.center.manage.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.SnapshotInfo;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.SnapshotInsertDTO;
import org.travis.center.manage.service.SnapshotInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.travis.center.support.aspect.Log;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import javax.annotation.Resource;
import java.util.List;

/**
* (VSP.VSP_SNAPSHOT_INFO)表控制层
*
* @author travis-wei
*/
@Slf4j
@RestController
@RequestMapping("/snapshot")
public class SnapshotInfoController {

    @Resource
    private SnapshotInfoService snapshotInfoService;

    @Log(title = "查询快照列表信息", businessType = BusinessTypeEnum.QUERY, isSaveResponseData = false)
    @Operation(summary = "查询快照列表信息")
    @GetMapping("/select")
    public List<SnapshotInfo> selectSnapshotList() {
        return snapshotInfoService.selectSnapshotList();
    }

    @Log(title = "分页查询快照列表信息", businessType = BusinessTypeEnum.QUERY, isSaveResponseData = false)
    @Operation(summary = "分页查询快照列表信息")
    @GetMapping("/pageSelect")
    public PageResult<SnapshotInfo> pageSelectSnapshotList(@Validated @RequestBody PageQuery pageQuery) {
        return snapshotInfoService.pageSelectSnapshotList(pageQuery);
    }

    @Log(title = "创建虚拟机快照", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "创建虚拟机快照")
    @PostMapping("/create")
    public void createSnapshotInfo(@Validated @RequestBody SnapshotInsertDTO snapshotInsertDTO) {
        snapshotInfoService.createSnapshotInfo(snapshotInsertDTO);
    }

}
