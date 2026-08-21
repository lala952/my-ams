package com.ruoyi.asset.constant;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池常量配置
 *
 * @author wangqin
 */
public class ThreadPoolExecutorConstants {

    private ThreadPoolExecutorConstants() {
    }

    /**
     * 1. 获取 CPU 核数
     */
    private static final int CPU_CORE = Runtime.getRuntime().availableProcessors();

    /**
     * 2. 根据任务类型设置核心线程数（IO密集型）
     * CPU密集型 则为 CPU核数 + 1
     * IO密集型 则为 CPU核数的2倍
     */
    private static final int CORE_POOL_SIZE = CPU_CORE * 2;
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 60L;

    /**
     * 3. 创建线程池
     */
    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "change-pool-thread-" + counter.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    /**
     * 线程池常量
     * 【修改点1】区分业务线程池和IO线程池
     */
    /**
     * 业务线程池（数据库批量操作）
     */
    public static ExecutorService BIZ_EXECUTOR;

    /**
     * IO密集型线程池（附件、PDF等）
     */
    public static ExecutorService IO_EXECUTOR;
}