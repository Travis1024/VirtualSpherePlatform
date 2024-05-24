package org.travis.center.manage.service;

import org.travis.center.common.entity.manage.NetworkLayerInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.manage.pojo.dto.NetworkInsertDTO;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;

import java.util.List;

/**
 * @ClassName NetworkLayerInfoService
 * @Description NetworkLayerInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface NetworkLayerInfoService extends IService<NetworkLayerInfo>{
    NetworkLayerInfo insertOne(NetworkInsertDTO networkInsertDTO);

    void deleteOne(Long networkLayerId);

    List<NetworkLayerInfo> selectList();

    PageResult<NetworkLayerInfo> pageSelectList(PageQuery pageQuery);
}
