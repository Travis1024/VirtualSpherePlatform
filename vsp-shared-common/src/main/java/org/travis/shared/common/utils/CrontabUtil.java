package org.travis.shared.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.cron.CronException;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @ClassName CrontabUtil
 * @Description CrontabUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/31
 */
public class CrontabUtil {
    /**
     * 获取两次执行时间间隔秒数
     *
     * @param crontabExpression cron 表达式
     * @return  间隔秒数
     */
    public static long getCrontabIntervalInSeconds(String crontabExpression) {
        try {
            // 获取当前时间
            LocalDateTime localDateTime = DateUtil.date().toLocalDateTime();
            // 解析 cron 表达式
            CronExpression parsed = CronExpression.parse(crontabExpression);
            // 获取下一次执行时间
            LocalDateTime next = parsed.next(localDateTime);
            // 获取下下次执行时间
            LocalDateTime nextNext = parsed.next(next);
            // 计算两次间隔的时间秒数
            return next.until(nextNext, ChronoUnit.SECONDS);
        } catch (Exception e) {
            throw new CronException(e.getMessage(), e);
        }
    }
}
