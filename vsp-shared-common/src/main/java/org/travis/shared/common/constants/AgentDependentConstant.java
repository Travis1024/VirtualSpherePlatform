package org.travis.shared.common.constants;

/**
 * @ClassName AgentDependentConstant
 * @Description AgentDependentConstant
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
public class AgentDependentConstant {
    /**
     * 桥接网络初始化文件 key
     */
    public static final String INIT_BRIDGE_KEY = "init-bridge";
    /**
     * virsh 虚拟网络初始化文件 key
     */
    public static final String INIT_VIRSH_NETWORK_KEY = "init-virsh-network";

    /**
     * virsh 计算虚拟 CPU 核数 key
     */
    public static final String INIT_VIRSH_CPU_NUMBER_KEY = "init-virsh-cpu-calc";

    /**
     * 查询磁盘大小 key
     */
    public static final String INIT_DISK_SIZE_CALC_KEY = "init-disk-size-calc";
}
