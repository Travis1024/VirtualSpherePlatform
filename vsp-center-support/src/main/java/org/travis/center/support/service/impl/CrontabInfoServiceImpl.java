package org.travis.center.support.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.travis.center.common.entity.support.CrontabInfo;
import org.travis.center.common.enums.IsFixedEnum;
import org.travis.center.common.mapper.support.CrontabInfoMapper;
import org.travis.center.support.pojo.dto.CrontabUpdateDTO;
import org.travis.center.support.service.CrontabInfoService;
import org.travis.shared.common.constants.CrontabConstant;
import org.travis.shared.common.constants.RedissonConstant;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.ForbiddenException;
import org.travis.shared.common.exceptions.NotFoundException;
import org.travis.shared.common.utils.CrontabUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    public RedissonClient redissonClient;

    @Override
    public List<CrontabInfo> selectList() {
        return getBaseMapper().selectList(null);
    }

    @Override
    public void updateCronExpression(CrontabUpdateDTO crontabUpdateDTO) {
        // 1.查询定时任务信息，按断判断是否允许修改
        Optional<CrontabInfo> optionalCrontabInfo = Optional.ofNullable(getById(crontabUpdateDTO.getId()));
        Assert.isTrue(optionalCrontabInfo.isPresent(), () -> new NotFoundException("未找到相关定时任务!"));
        Assert.isTrue(IsFixedEnum.ALLOW_UPDATE.getValue().equals(optionalCrontabInfo.get().getIsFixed().getValue()), () -> new ForbiddenException("当前定时任务 Crontab 表达式禁止修改!"));
        // 2.校验 CronExpression 表达式是否合法
        Assert.isTrue(CronExpression.isValidExpression(crontabUpdateDTO.getCronExpression()), () -> new BadRequestException("Crontab 表达式校验失败!"));
        // 3.更新表达式
        // 3.1.更新数据库
        getBaseMapper().update(
                Wrappers.<CrontabInfo>lambdaUpdate()
                        .set(CrontabInfo::getCronExpression, crontabUpdateDTO.getCronExpression())
                        .set(CrontabInfo::getCronDescription, crontabUpdateDTO.getCronDescription())
                        .eq(CrontabInfo::getId, crontabUpdateDTO.getId())
        );
        // 3.2.删除 redis 缓存
        RMap<Long, CrontabInfo> rMap = redissonClient.getMap(RedissonConstant.CRONTAB_CACHE_KEY);
        rMap.remove(crontabUpdateDTO.getId());
    }
}
