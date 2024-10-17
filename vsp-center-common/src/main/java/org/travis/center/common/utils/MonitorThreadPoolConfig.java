package org.travis.center.common.utils;

import cn.hutool.core.thread.ExecutorBuilder;
import org.travis.shared.common.constants.ThreadPoolConstant;
import org.travis.shared.common.utils.ThreadPoolExecutorShutdownUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MonitorThreadPoolConfig
 * @Description MonitorThreadPoolConfig
 * @Author travis-wei
 * @Version v1.0
 */
public class MonitorThreadPoolConfig {

    public static ExecutorService monitorProcessExecutor = null;

    static {
        // 线程池初始化
        initMessageProcessExecutor();
        // 线程池关闭管理
        ThreadPoolExecutorShutdownUtil.registryExecutor(monitorProcessExecutor);
    }

    private static void initMessageProcessExecutor() {
        monitorProcessExecutor = ExecutorBuilder.create()
                // 核心线程数量（初始化 8）
                .setCorePoolSize(ThreadPoolConstant.SMALL_CORE_POOL_SIZE)
                // 最大线程数量（初始化 16）
                .setMaxPoolSize(ThreadPoolConstant.SMALL_MAX_POOL_SIZE)
                // 空闲线程等待新任务的最长时间（初始化 30 秒）
                .setKeepAliveTime(ThreadPoolConstant.SMALL_KEEP_LIVE_TIME, TimeUnit.SECONDS)
                // 拒绝策略：调用者运行策略
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                // 是否允许核心线程空闲超时时被回收
                .setAllowCoreThreadTimeOut(true)
                // 设置工作队列（队列大小：512）
                .setWorkQueue(new LinkedBlockingQueue<>(ThreadPoolConstant.BIG_QUEUE_SIZE))
                .build();
    }
}
