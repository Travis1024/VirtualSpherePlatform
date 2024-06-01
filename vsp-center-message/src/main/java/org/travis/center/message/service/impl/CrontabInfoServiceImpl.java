package org.travis.center.message.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.travis.center.common.entity.message.CrontabInfo;
import org.travis.center.common.mapper.message.CrontabInfoMapper;
import org.travis.center.message.pojo.dto.CrontabUpdateDTO;
import org.travis.center.message.service.CrontabInfoService;
import org.travis.shared.common.constants.CrontabConstant;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.NotFoundException;
import org.travis.shared.common.utils.CrontabUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName CrontabInfoServiceImpl
 * @Description CrontabInfoServiceImpl
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
@Service
public class CrontabInfoServiceImpl extends ServiceImpl<CrontabInfoMapper, CrontabInfo> implements CrontabInfoService{

    @Resource
    private RedissonClient redissonClient;

    @Override
    public List<CrontabInfo> selectList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public void updateCronExpression(CrontabUpdateDTO crontabUpdateDTO) {
        // 1.校验 CronExpression 表达式是否合法
        Assert.isTrue(CronExpression.isValidExpression(crontabUpdateDTO.getCronExpression()), () -> new BadRequestException("Crontab 表达式校验失败!"));
        // 2.获取执行周期
        long intervalInSeconds = CrontabUtil.getCrontabIntervalInSeconds(crontabUpdateDTO.getCronExpression());
        // 3.查询目前定时任务信息，并进行替换
        CrontabInfo crontabInfo = getById(crontabUpdateDTO.getId());
        Assert.isTrue(ObjectUtil.isNotEmpty(crontabInfo), () -> new NotFoundException("未找到定时任务信息!"));
        // 4.更新表达式
        // 4.1.更新数据库
        getBaseMapper().update(
                Wrappers.<CrontabInfo>lambdaUpdate()
                        .set(CrontabInfo::getCronExpression, crontabUpdateDTO.getCronExpression())
                        .set(CrontabInfo::getCronDescription, StrUtil.format(CrontabConstant.CRON_DESCRIPTION_TEMPLATE, intervalInSeconds))
                        .eq(CrontabInfo::getId, crontabUpdateDTO.getId())
        );
        // 4.2.删除 redis 缓存
        RMap<Long, CrontabInfo> rMap = redissonClient.getMap(RedissonConstant.CRONTAB_CACHE_KEY);
        rMap.remove(crontabUpdateDTO.getId());
    }
}
