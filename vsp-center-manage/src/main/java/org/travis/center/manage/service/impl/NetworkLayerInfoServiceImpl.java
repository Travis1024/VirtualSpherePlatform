package org.travis.center.manage.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.manage.HostInfo;
import org.travis.center.common.mapper.manage.HostInfoMapper;
import org.travis.center.common.mapper.manage.NetworkLayerInfoMapper;
import org.travis.center.common.entity.manage.NetworkLayerInfo;
import org.travis.center.manage.pojo.dto.NetworkInsertDTO;
import org.travis.center.manage.service.NetworkLayerInfoService;
import org.travis.shared.common.domain.PageQuery;
import org.travis.shared.common.domain.PageResult;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.utils.SnowflakeIdUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName NetworkLayerInfoServiceImpl
 * @Description NetworkLayerInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
@Service
public class NetworkLayerInfoServiceImpl extends ServiceImpl<NetworkLayerInfoMapper, NetworkLayerInfo> implements NetworkLayerInfoService{
    @Resource
    private HostInfoMapper hostInfoMapper;

    @Override
    public NetworkLayerInfo insertOne(NetworkInsertDTO networkInsertDTO) {
        // 1.判断网卡名称是否已经存在
        Optional<NetworkLayerInfo> layerInfoOptional = Optional.ofNullable(getOne(Wrappers.<NetworkLayerInfo>lambdaQuery().select(NetworkLayerInfo::getId).eq(NetworkLayerInfo::getNicName, networkInsertDTO.getNicName())));
        Assert.isTrue(layerInfoOptional.isEmpty(), () -> new BadRequestException("网卡名称已存在!"));
        // 2.保存数据
        NetworkLayerInfo networkLayerInfo = new NetworkLayerInfo();
        BeanUtils.copyProperties(networkInsertDTO, networkLayerInfo);
        networkLayerInfo.setId(SnowflakeIdUtil.nextId());
        save(networkLayerInfo);
        return networkLayerInfo;
    }

    @Override
    public void deleteOne(Long networkLayerId) {
        // 1.查询是否有宿主机依赖于当前二层网络
        Optional<HostInfo> hostInfoOptional = Optional.ofNullable(hostInfoMapper.selectOne(Wrappers.<HostInfo>lambdaQuery().eq(HostInfo::getNetworkLayerId, networkLayerId)));
        Assert.isTrue(hostInfoOptional.isEmpty(), () -> new BadRequestException("删除失败, 存在宿主机资源依赖于当前二层网络!"));

        // 2.删除当前网络信息
        removeById(networkLayerId);
    }

    @Override
    public List<NetworkLayerInfo> selectList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public PageResult<NetworkLayerInfo> pageSelectList(PageQuery pageQuery) {
        Page<NetworkLayerInfo> networkLayerInfoPage = getBaseMapper().selectPage(pageQuery.toMpPage(), null);
        return PageResult.of(networkLayerInfoPage);
    }
}
