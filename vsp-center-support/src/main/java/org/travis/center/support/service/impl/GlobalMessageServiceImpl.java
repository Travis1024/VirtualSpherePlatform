package org.travis.center.support.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.support.GlobalMessageMapper;
import org.travis.center.common.entity.support.GlobalMessage;
import org.travis.center.support.service.GlobalMessageService;
/**
 * @ClassName GlobalMessageServiceImpl
 * @Description GlobalMessageServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
@Service
public class GlobalMessageServiceImpl extends ServiceImpl<GlobalMessageMapper, GlobalMessage> implements GlobalMessageService{

}
