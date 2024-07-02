package org.travis.center.manage.creation;

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
    private static final Map<Integer, AbstractCreationService> CREATION_HOLDER = new ConcurrentHashMap<>(2);

    public static AbstractCreationService getCreationService(Integer vmwareCreateFormValue) {
        return CREATION_HOLDER.get(vmwareCreateFormValue);
    }

    public static void addCreationService(Integer vmwareCreateFormValue, AbstractCreationService creationService) {
        CREATION_HOLDER.put(vmwareCreateFormValue, creationService);
    }
}
