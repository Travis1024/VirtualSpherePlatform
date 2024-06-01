package org.travis.center.message.service;

import org.travis.center.common.entity.message.CrontabInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.travis.center.message.pojo.dto.CrontabUpdateDTO;

import java.util.List;

/**
 * @ClassName CrontabInfoService
 * @Description CrontabInfoService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
public interface CrontabInfoService extends IService<CrontabInfo>{

    List<CrontabInfo> selectList();

    void updateCronExpression(CrontabUpdateDTO crontabUpdateDTO);
}
