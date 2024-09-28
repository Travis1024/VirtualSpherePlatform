package org.travis.center.manage.template.vmware.build;

import org.travis.center.common.enums.VmwareCreateFormEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CreationHolder
 * @Description CreationHolder
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/29
 */
public class CreationHolder {
    private static final Map<VmwareCreateFormEnum, AbstractVmwareCreationService> CREATION_HOLDER = new ConcurrentHashMap<>(2);

    public static AbstractVmwareCreationService getCreationService(VmwareCreateFormEnum vmwareCreateFormEnum) {
        return CREATION_HOLDER.get(vmwareCreateFormEnum);
    }

    public static void addCreationService(VmwareCreateFormEnum vmwareCreateFormEnum, AbstractVmwareCreationService creationService) {
        CREATION_HOLDER.put(vmwareCreateFormEnum, creationService);
    }
}
