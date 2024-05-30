package org.travis.center.message.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.mapper.message.GlobalMessageMapper;
import org.travis.center.common.entity.message.GlobalMessage;
import org.travis.center.message.service.GlobalMessageService;
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
