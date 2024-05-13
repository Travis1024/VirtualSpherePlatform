package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.HostInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.HostInsertDTO;
import org.travis.center.manage.pojo.dto.HostSshCheckDTO;

/**
 * @ClassName HostInfoService
 * @Description HostInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface HostInfoService extends IService<HostInfo> {
    HostInfo insertOne(HostInsertDTO hostInsertDTO);

    boolean checkHostSshConnect(String hostIp, Integer hostSshPort, String username, String password);
}
