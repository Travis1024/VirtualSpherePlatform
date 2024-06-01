package org.travis.shared.common.utils;

import cn.hutool.core.date.DateUtil;

/**
 * @ClassName TimeUtil
 * @Description TimeUtil
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/6/1
 */
public class TimeUtil {

    public static String getCurrentYearMonth() {
        // 使用DateUtil.format方法格式化当前日期为"yyyyMM"格式
        return DateUtil.format(DateUtil.date(), "yyyyMM");
    }
}
