package org.travis.center.manage.service.impl;

import org.travis.center.manage.service.ResourceAllocationService;
import org.travis.shared.common.domain.R;

/**
 * @ClassName ResourceAllocationServiceImpl
 * @Description ResourceAllocationServiceImpl
 * @Author Travis
 * @Data 2024/10
 */
public class ResourceAllocationServiceImpl implements ResourceAllocationService {
    // TODO 补充虚拟机资源扩展接口实现
    @Override
    public R<?> expandCpuResource(String vmwareUuid, Boolean autoFlag) {
        return null;
    }

    @Override
    public R<?> expandMemoryResource(String vmwareUuid, Boolean autoFlag) {
        return null;
    }

    @Override
    public R<?> reduceCpuResource(String vmwareUuid, Boolean autoFlag) {
        return null;
    }

    @Override
    public R<?> reduceMemoryResource(String vmwareUuid, Boolean autoFlag) {
        return null;
    }

    @Override
    public R<?> recommendCpuResource(String vmwareUuid, boolean addFlag) {
        return null;
    }

    @Override
    public R<?> recommendMemoryResource(String vmwareUuid, boolean addFlag) {
        return null;
    }
}
