package org.travis.center.manage.service;

import org.travis.shared.common.domain.R;

/**
 * @ClassName ResourceAllocationService
 * @Description 资源动态监控服务接口类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/14
 */
public interface ResourceAllocationService {
    R<?> expandCpuResource(String vmwareUuid, Boolean autoFlag);
    R<?> expandMemoryResource(String vmwareUuid, Boolean autoFlag);

    R<?> reduceCpuResource(String vmwareUuid, Boolean autoFlag);
    R<?> reduceMemoryResource(String vmwareUuid, Boolean autoFlag);

    R<?> recommendCpuResource(String vmwareUuid, boolean addFlag);
    R<?> recommendMemoryResource(String vmwareUuid, boolean addFlag);
}
