package org.travis.shared.common.constants;

/**
 * @ClassName ThreadPoolConstant
 * @Description ThreadPoolConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
public class ThreadPoolConstant {
    public static final Integer SMALL_CORE_POOL_SIZE = 4;
    public static final Integer SMALL_MAX_POOL_SIZE = 8;
    public static final Integer SMALL_KEEP_LIVE_TIME = 10;
    public static final Integer SMALL_QUEUE_SIZE = 32;


    public static final Integer MIDDLE_CORE_POOL_SIZE = 8;
    public static final Integer MIDDLE_MAX_POOL_SIZE = 16;
    public static final Integer MIDDLE_KEEP_LIVE_TIME = 30;
    public static final Integer MIDDLE_QUEUE_SIZE = 128;


    public static final Integer BIG_CORE_POOL_SIZE = 16;
    public static final Integer BIG_MAX_POOL_SIZE = 32;
    public static final Integer BIG_KEEP_LIVE_TIME = 60;
    public static final Integer BIG_QUEUE_SIZE = 512;


    public static final Integer SINGLE_CORE_POOL_SIZE = 1;
    public static final Integer SINGLE_MAX_POOL_SIZE = 1;
    public static final Integer SINGLE_KEEP_LIVE_TIME = SMALL_KEEP_LIVE_TIME;
    public static final Integer SINGLE_QUEUE_SIZE = MIDDLE_QUEUE_SIZE;
}
