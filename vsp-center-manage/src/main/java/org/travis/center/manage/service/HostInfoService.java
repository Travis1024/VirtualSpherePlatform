package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.HostInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.HostInsertDTO;
import org.travis.center.manage.pojo.dto.HostSshCheckDTO;
import org.travis.center.manage.pojo.dto.HostUpdateDTO;
import org.travis.center.manage.pojo.vo.HostErrorVO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName HostInfoService
 * @Description HostInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface HostInfoService extends IService<HostInfo> {
    HostInfo insertOne(HostInsertDTO hostInsertDTO) throws ExecutionException, InterruptedException;
    boolean validateHostSshConnect(String hostIp, Integer hostSshPort, String username, String password);
    List<HostErrorVO> delete(List<Long> hostIdList);
    HostErrorVO deleteOneById(Long hostId);
    void updateOne(HostUpdateDTO hostUpdateDTO);
    void updateHostIp(Long hostId, String hostIp);
    boolean validateHostAgentConnect(String ipAddr);
    PageResult<HostInfo> pageSelectList(PageQuery pageQuery);
    List<HostInfo> selectList();
    void correctHostState();
}
