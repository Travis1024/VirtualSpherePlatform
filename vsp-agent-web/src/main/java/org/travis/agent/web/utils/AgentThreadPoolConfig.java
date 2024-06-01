package org.travis.agent.web.utils;

import cn.hutool.core.thread.ExecutorBuilder;
import org.travis.shared.common.constants.ThreadPoolConstant;
import org.travis.shared.common.utils.ThreadPoolExecutorShutdownUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AgentThreadPoolConfig
 * @Description AgentThreadPoolConfig
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/25
 */
public class AgentThreadPoolConfig {
    public static ExecutorService singleExecutor = null;

    static {
        // 线程池初始化
        initSingleExecutor();
        // 线程池关闭管理
        ThreadPoolExecutorShutdownUtil.registryExecutor(singleExecutor);
    }


    private static void initSingleExecutor() {
        singleExecutor = ExecutorBuilder.create()
                // 核心线程数量（初始化 1）
                .setCorePoolSize(ThreadPoolConstant.SINGLE_CORE_POOL_SIZE)
                // 最大线程数量（初始化 1）
                .setMaxPoolSize(ThreadPoolConstant.SINGLE_MAX_POOL_SIZE)
                // 空闲线程等待新任务的最长时间（初始化 10 秒）
                .setKeepAliveTime(ThreadPoolConstant.SMALL_KEEP_LIVE_TIME, TimeUnit.SECONDS)
                // 拒绝策略：调用者运行策略
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                // 是否允许核心线程空闲超时时被回收
                .setAllowCoreThreadTimeOut(true)
                // 设置工作队列（队列大小：1024）
                .setWorkQueue(new LinkedBlockingQueue<>(ThreadPoolConstant.SMALL_QUEUE_SIZE))
                .build();
    }
}
