package org.travis.center.message.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.message.CrontabInfo;
import org.travis.center.common.mapper.message.CrontabInfoMapper;
import org.travis.center.message.service.CrontabInfoService;

import java.util.List;

/**
 * @ClassName CrontabInfoServiceImpl
 * @Description CrontabInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Service
public class CrontabInfoServiceImpl extends ServiceImpl<CrontabInfoMapper, CrontabInfo> implements CrontabInfoService{

    @Override
    public List<CrontabInfo> selectList() {
        return getBaseMapper().selectList(null);
    }
}
