package org.travis.center.manage.expose;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import org.travis.api.client.center.CenterVmwareClient;
import org.travis.center.manage.service.VmwareInfoService;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.CommonException;

import javax.annotation.Resource;

/**
 * @ClassName CenterVmwareClientImpl
 * @Description CenterVmwareClientImpl
 * @Author Travis
 * @Data 2024/10
 */
@Component
public class CenterVmwareClientImpl implements CenterVmwareClient {

    @Resource
    private VmwareInfoService vmwareInfoService;

    @Override
    public R<String> queryIpAddr(Long vmwareId) {
        try {
            String ipAddress = vmwareInfoService.queryIpAddress(vmwareId);
            if (StrUtil.isEmpty(ipAddress)) {
                throw new CommonException(BizCodeEnum.UNKNOW.getCode(), "IP地址为空！");
            }
            return R.ok(ipAddress);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
