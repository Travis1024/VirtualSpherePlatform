package org.travis.center.common.mapper.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.travis.center.common.entity.support.GlobalMessage;

/**
 * @ClassName GlobalMessageMapper
 * @Description GlobalMessageMapper
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Mapper
public interface GlobalMessageMapper extends BaseMapper<GlobalMessage> {
}
