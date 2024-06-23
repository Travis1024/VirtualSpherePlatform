package org.travis.center.common.mapper.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.support.ScheduleJob;

/**
 * @ClassName ScheduleJobMapper
 * @Description ScheduleJobMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/23
 */
@Mapper
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {
}
