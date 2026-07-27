package com.ruoyi.asset.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 * 【修改点1】线程池隔离：业务线程池 + IO密集型线程池分离
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 业务线程池（用于数据库批量操作）
     * 参数计算：核心线程 = CPU核数；最大线程 = CPU核数 * 2
     */
    @Bean("bizExecutor")
    public ThreadPoolExecutor bizExecutor() {
        int coreSize = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                coreSize,                                    // 核心线程数
                coreSize * 2,                                // 最大线程数
                60, TimeUnit.SECONDS,                        // 空闲线程存活时间
                new LinkedBlockingQueue<>(500),               // 队列容量
                new NamedThreadFactory("biz-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy()     // 拒绝策略：由调用线程执行
        );
    }

    /**
     * IO密集型线程池（用于附件保存、PDF导出等）
     * 参数计算：核心线程 = CPU核数 * 2；最大线程 = CPU核数 * 4
     */
    @Bean("ioExecutor")
    public ThreadPoolExecutor ioExecutor() {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        return new ThreadPoolExecutor(
                coreSize,
                coreSize * 2,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500),
                new NamedThreadFactory("io-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 自定义线程工厂，便于日志追踪线程
     */
    static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}