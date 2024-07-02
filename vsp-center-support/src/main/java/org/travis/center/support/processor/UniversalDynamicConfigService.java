package org.travis.center.support.processor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.support.DynamicConfigInfo;
import org.travis.center.common.enums.DynamicConfigTypeEnum;

/**
 * @ClassName UniversalDynamicConfigService
 * @Description 通用动态配置更新实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/7/2
 */
@Component
public class UniversalDynamicConfigService extends AbstractDynamicConfigService {

    public UniversalDynamicConfigService() {
        dynamicConfigTypeEnum = DynamicConfigTypeEnum.UNIVERSAL;
    }

    @Override
    public void updateConfigValue(String configValue) {
        // 1.更新持久化配置
        dynamicConfigInfoMapper.update(
                Wrappers.<DynamicConfigInfo>lambdaUpdate()
                        .set(DynamicConfigInfo::getConfigValue, configValue)
                        .eq(DynamicConfigInfo::getId, dynamicConfigInfo.getId())
        );
        // 2.删除本地缓存
        configPermanentCache.invalidate(dynamicConfigInfo.getId());
    }

    @Transactional
    @Override
    public void insertConfigValue() {
        // 1.持久化到数据库中
        dynamicConfigInfoMapper.insert(dynamicConfigInfo);
        // 2.缓存到 Caffeine
        configPermanentCache.put(dynamicConfigInfo.getId(), dynamicConfigInfo.getConfigValue());
    }
}
