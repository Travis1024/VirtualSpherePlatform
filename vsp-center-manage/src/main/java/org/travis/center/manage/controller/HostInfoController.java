package org.travis.center.manage.controller;
import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.travis.center.common.entity.manage.HostInfo;
import org.springframework.web.bind.annotation.*;
import org.travis.center.common.enums.BusinessTypeEnum;
import org.travis.center.manage.pojo.dto.HostInsertDTO;
import org.travis.center.manage.pojo.dto.HostSshCheckDTO;
import org.travis.center.manage.pojo.dto.HostUpdateDTO;
import org.travis.center.manage.pojo.vo.HostErrorVO;
import org.travis.center.manage.service.HostInfoService;
import org.travis.center.support.aspect.Log;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.VspStrUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    @Log(title = "通过ID查询单条宿主机数据", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "通过ID查询单条宿主机数据")
    @GetMapping("/selectOne")
    public HostInfo selectOne(Long hostId) {
        Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoService.getById(hostId));
        return hostInfoOptional.orElseThrow(() -> new BadRequestException("未查询到宿主机信息!"));
    }

    @Log(title = "新增单条宿主机数据", businessType = BusinessTypeEnum.INSERT)
    @Operation(summary = "新增单条宿主机数据")
    @PostMapping("/insertOne")
    public HostInfo insertOne(@Validated @RequestBody HostInsertDTO hostInsertDTO) throws ExecutionException, InterruptedException {
        VspStrUtil.trimStr(hostInsertDTO);
        return hostInfoService.insertOne(hostInsertDTO);
    }

    @Log(title = "宿主机信息删除", businessType = BusinessTypeEnum.DELETE)
    @Operation(summary = "宿主机信息删除")
    @DeleteMapping("/delete")
    public List<HostErrorVO> delete(@RequestParam("hostIds") List<Long> hostIdList) {
        Assert.isFalse(hostIdList.isEmpty(), () -> new BadRequestException("宿主机 ID 列表为空!"));
        return hostInfoService.delete(hostIdList);
    }

    @Log(title = "宿主机信息更新", businessType = BusinessTypeEnum.UPDATE)
    @Operation(summary = "宿主机信息更新")
    @PutMapping("/update")
    public void updateOne(@Validated @RequestBody HostUpdateDTO hostUpdateDTO) {
        hostInfoService.updateOne(hostUpdateDTO);
    }

    @Log(title = "校验新增宿主机IP及Agent健康状态")
    @Operation(summary = "校验新增宿主机IP及Agent健康状态")
    @GetMapping("/ipPreCheck")
    public boolean validateHostAgentConnect(@RequestParam("newIpAddr") String ipAddr) {
        return hostInfoService.validateHostAgentConnect(ipAddr);
    }

    @Log(title = "宿主机SSH连接预检测")
    @Operation(summary = "宿主机SSH连接预检测")
    @PostMapping("/sshPreCheck")
    public boolean validateHostSshConnect(@Validated @RequestBody HostSshCheckDTO hostSshCheckDTO) {
        return hostInfoService.validateHostSshConnect(
                hostSshCheckDTO.getHostIp(),
                hostSshCheckDTO.getHostSshPort(),
                hostSshCheckDTO.getUsername(),
                hostSshCheckDTO.getPassword()
        );
    }

    @Log(title = "分页查询宿主机信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "分页查询宿主机信息列表")
    @PostMapping("/pageSelect")
    public PageResult<HostInfo> pageSelectList(@Validated @RequestBody PageQuery pageQuery) {
        return hostInfoService.pageSelectList(pageQuery);
    }

    @Log(title = "查询宿主机信息列表", businessType = BusinessTypeEnum.QUERY)
    @Operation(summary = "查询宿主机信息列表")
    @GetMapping("/select")
    public List<HostInfo> selectList() {
        return hostInfoService.selectList();
    }
}
