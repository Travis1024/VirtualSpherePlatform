package org.travis.shared.common.constants;

/**
 * @ClassName ThreadPoolConstant
 * @Description ThreadPoolConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
public class ThreadPoolConstant {
    public static final Integer SINGLE_CORE_POOL_SIZE = 1;
    public static final Integer SINGLE_MAX_POOL_SIZE = 1;
    public static final Integer SMALL_KEEP_LIVE_TIME = 10;

    /**
     * small queue size
     */
    public static final Integer SMALL_QUEUE_SIZE = 32;

    /**
     * big queue size
     */
    public static final Integer BIG_QUEUE_SIZE = 1024;
}
