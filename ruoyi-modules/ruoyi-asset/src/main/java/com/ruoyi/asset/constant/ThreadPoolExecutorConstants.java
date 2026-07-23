package com.ruoyi.asset.constant;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池常量配置
 */
public class ThreadPoolExecutorConstants {

    private ThreadPoolExecutorConstants() {
    }

    // 1. 获取 CPU 核数
    private static final int CPU_CORE = Runtime.getRuntime().availableProcessors();

    // 2. 根据任务类型设置核心线程数（IO密集型）
    private static final int CORE_POOL_SIZE = CPU_CORE * 2;
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 60L;

    // 3. 创建线程池
    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            CORE_POOL_SIZE,                                    // 核心线程数
            MAXIMUM_POOL_SIZE,                                 // 最大线程数
            KEEP_ALIVE_TIME,                                   // 空闲线程存活时间
            TimeUnit.SECONDS,                                  // 时间单位
            new ArrayBlockingQueue<>(100),                     // 有界队列（防止OOM）
            new ThreadFactory() {                              // 线程工厂（给线程起名字）
                private final AtomicInteger counter = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "change-pool-thread-" + counter.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()          // 拒绝策略
    );
}