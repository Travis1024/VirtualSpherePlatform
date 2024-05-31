package org.travis.center.common.mapper.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.message.CrontabInfo;

/**
 * @ClassName CrontabInfoMapper
 * @Description CrontabInfoMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Mapper
public interface CrontabInfoMapper extends BaseMapper<CrontabInfo> {
}
