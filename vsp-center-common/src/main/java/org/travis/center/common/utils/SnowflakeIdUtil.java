package org.travis.center.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @ClassName SnowflakeIdUtil
 * @Description 雪花算法依赖类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/2/26
 */
public class SnowflakeIdUtil {
    private static final long WORKER_ID = 1L;
    private static final long DATA_CENTER_ID = 1L;
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(WORKER_ID, DATA_CENTER_ID);

    public static long nextId() {
        return SNOWFLAKE.nextId();
    }
}
