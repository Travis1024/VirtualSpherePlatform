package org.travis.center.common.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.monitor.IpmiLogInfo;

/**
 * @ClassName IpmiLogInfoMapper
 * @Description IpmiLogInfoMapper
 * @Author Travis
 * @Data 2024/10
 */
@Mapper
public interface IpmiLogInfoMapper extends BaseMapper<IpmiLogInfo> {
}