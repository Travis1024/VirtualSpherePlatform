package org.travis.center.manage.pojo.pipeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.entity.manage.VmwareInfo;
import org.travis.shared.common.pipeline.ProcessModel;

import java.io.Serializable;

/**
 * @ClassName VmwareDestroyPipe
 * @Description VmwareDestroyPipe
 * @Author Travis
 * @Data 2024/09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VmwareDestroyPipe implements Serializable, ProcessModel {
    private Long vmwareId;
    private VmwareInfo vmwareInfo;
    private HostInfo hostInfo;
}
