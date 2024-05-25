package org.travis.center.manage.service.dubbo;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.travis.api.client.center.CenterHostClient;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.enums.HostStateEnum;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.shared.common.domain.R;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.BadRequestException;

import javax.annotation.Resource;

/**
 * @ClassName CenterHostClientImpl
 * @Description CenterHostClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
@Slf4j
@DubboService
public class CenterHostClientImpl implements CenterHostClient {
    @Resource
    private HostInfoMapper hostInfoMapper;

    @Override
    public R<Void> sendBridgedInitMessage(Long hostId, boolean isSuccess, String stateMessage) {
        try {
            int updated = hostInfoMapper.update(
                    Wrappers.<HostInfo>lambdaUpdate()
                            .eq(HostInfo::getId, hostId)
                            .set(HostInfo::getStateMessage, stateMessage)
                            .set(HostInfo::getState, isSuccess ? HostStateEnum.READY : HostStateEnum.ERROR)
            );
            Assert.isTrue(updated != 0, () -> new BadRequestException("宿主机状态更新失败, 未找到当前宿主机信息!"));
            return R.ok();
        } catch (Exception e) {
            log.error("[CenterHostClientImpl::sendBridgedInitMessage] Modify Host Bridged Init State Error!");
            return R.error(BizCodeEnum.DUBBO_FUNCTION_ERROR.getCode(), e.getMessage());
        }
    }
}
