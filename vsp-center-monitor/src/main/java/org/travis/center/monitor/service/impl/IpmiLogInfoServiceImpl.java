package org.travis.center.monitor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.monitor.IpmiLogInfo;
import org.travis.center.common.mapper.monitor.IpmiLogInfoMapper;
import org.travis.center.monitor.service.IpmiLogInfoService;
/**
 * @ClassName IpmiLogInfoServiceImpl
 * @Description IpmiLogInfoServiceImpl
 * @Author Travis
 * @Data 2024/10
 */
@Service
public class IpmiLogInfoServiceImpl extends ServiceImpl<IpmiLogInfoMapper, IpmiLogInfo> implements IpmiLogInfoService{

}
