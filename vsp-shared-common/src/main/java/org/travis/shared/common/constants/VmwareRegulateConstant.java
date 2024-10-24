package org.travis.shared.common.constants;

/**
 * @ClassName VmLiveDataConstant
 * @Description 虚拟机 CPU、内存实时状态常量类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/13
 */
public class VmwareRegulateConstant {

    public static final String LIVE_DATA_KEY_PREFIX = "live:";
    public static final String CPU_HIGH = "cpu_high:";
    public static final String MEMORY_HIGH = "memory_high:";
    public static final String CPU_LOW = "cpu_low:";
    public static final String MEMORY_LOW = "memory_low:";

    public static final String RECOMMEND_CPU_KEY_PREFIX = LIVE_DATA_KEY_PREFIX + "recommend:cpu:";
    public static final String RECOMMEND_MEM_KEY_PREFIX = LIVE_DATA_KEY_PREFIX + "recommend:memory:";

    public static final String UPDATE_CPU_KEY_PREFIX = LIVE_DATA_KEY_PREFIX + "update:cpu:";
    public static final String UPDATE_MEM_KEY_PREFIX = LIVE_DATA_KEY_PREFIX + "update:memory:";

}
