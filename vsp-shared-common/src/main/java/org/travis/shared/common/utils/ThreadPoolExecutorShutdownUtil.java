package org.travis.shared.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolExecutorShutdownConfig
 * @Description 线程池关闭管理
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/11/19
 */
@Slf4j
public class ThreadPoolExecutorShutdownUtil implements ApplicationListener<ContextClosedEvent> {

    private static final List<ExecutorService> POOLS = Collections.synchronizedList(new ArrayList<>());
    /**
     * 线程中的任务在接收到应用关闭信号量后最多等待多久「10 秒」就强制终止，其实就是给剩余任务预留的时间，到时间后线程池必须销毁!
     */
    private static final long AWAIT_TERMINATION_TIME = 10;


    /**
     * 将线程池加入 Spring 管理
     * @param executor
     */
    public static void registryExecutor(ExecutorService executor) {
        POOLS.add(executor);
    }

    /**
     * 参考{@link org.springframework.scheduling.concurrent.ExecutorConfigurationSupport#shutdown()}
     *
     * 当 ApplicationContext 关闭时触发
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("[ThreadPools-Close] ApplicationContext close！Start closing the thread pool！Number：{}", POOLS.size());
        if (POOLS.isEmpty()) {
            return;
        }
        for (ExecutorService pool : POOLS) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(AWAIT_TERMINATION_TIME, TimeUnit.SECONDS)) {
                    log.warn("Timed out while waiting for executor [{}] to terminate!", pool);
                }
            } catch (Exception e) {
                log.warn("Timed out while waiting for executor [{}] to terminate!", pool);
                Thread.currentThread().interrupt();
            }
        }
    }
}
