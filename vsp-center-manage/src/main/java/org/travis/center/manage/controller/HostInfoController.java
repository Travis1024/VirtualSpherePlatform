package org.travis.center.manage.controller;
import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.HostInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.manage.pojo.dto.HostInsertDTO;
import org.travis.center.manage.pojo.dto.HostSshCheckDTO;
import org.travis.center.manage.service.HostInfoService;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
* (VSP.VSP_HOST_INFO)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/host")
public class HostInfoController {

    @Resource
    private HostInfoService hostInfoService;

    @Operation(summary = "通过ID查询单条宿主机数据")
    @GetMapping("/selectOne")
    public HostInfo selectOne(Long hostId) {
        Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoService.getById(hostId));
        return hostInfoOptional.orElseThrow(() -> new BadRequestException("未查询到宿主机信息!"));
    }

    @Operation(summary = "新增单条宿主机数据")
    @PostMapping("/insertOne")
    public HostInfo insertOne(@Validated @RequestBody HostInsertDTO hostInsertDTO) {
        VspStrUtil.trimStr(hostInsertDTO);
        return hostInfoService.insertOne(hostInsertDTO);
    }

    @Operation(summary = "宿主机信息删除")
    @DeleteMapping("/delete")
    public void delete(@RequestParam("hostIds") List<Long> hostIdList) {
        if (!hostIdList.isEmpty()) {
            hostInfoService.delete(hostIdList);
        }
    }

    @Operation(summary = "宿主机 SSH 连接预检测")
    @PostMapping("/sshPreCheck")
    public boolean checkHostSshConnect(@Validated @RequestBody HostSshCheckDTO hostSshCheckDTO) {
        return hostInfoService.checkHostSshConnect(
                hostSshCheckDTO.getHostIp(),
                hostSshCheckDTO.getHostSshPort(),
                hostSshCheckDTO.getUsername(),
                hostSshCheckDTO.getPassword()
        );
    }
}
