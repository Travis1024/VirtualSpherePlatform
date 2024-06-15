package org.travis.center.web.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.travis.api.client.center.CenterHealthyClient;
import org.travis.api.pojo.bo.HostHealthyStateBO;
import org.travis.shared.common.domain.R;

/**
 * @ClassName CenterHealthyClientImpl
 * @Description CenterHealthyClientImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/15
 */
@Slf4j
@DubboService
public class CenterHealthyClientImpl implements CenterHealthyClient {
    @Override
    public R<Void> dubboHealthyCheck() {
        log.info("[center] dubbo healthy check");
        return R.ok();
    }

    @Override
    public R<Void> pushHostHealthyState(HostHealthyStateBO hostHealthyStateBO) {
        try {


            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.error(e.toString());
        }
    }
}
