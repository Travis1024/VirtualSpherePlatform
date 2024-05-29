package org.travis.center.manage.creation;

import org.springframework.stereotype.Component;
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
@Component
public class CreationHolder {
    private final Map<Integer, AbstractCreationService> creationHolder = new ConcurrentHashMap<>(2);

    public AbstractCreationService getCreationService(Integer vmwareCreateFormValue) {
        return creationHolder.get(vmwareCreateFormValue);
    }

    public void addCreationService(Integer vmwareCreateFormValue, AbstractCreationService creationService) {
        creationHolder.put(vmwareCreateFormValue, creationService);
    }
}
