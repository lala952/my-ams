package com.ruoyi.asset.utils;

import com.github.rholder.retry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 重试工具类
 * 【修改点2】基于 Guava Retry 封装，用于数据库批量操作失败重试
 */
public class RetryUtils {

    private static final Logger log = LoggerFactory.getLogger(RetryUtils.class);

    /** 数据库操作重试器：最多重试 3 次，间隔 500ms */
    private static final Retryer<Boolean> DB_RETRYER = RetryerBuilder.<Boolean>newBuilder()
            .retryIfException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .withWaitStrategy(WaitStrategies.fixedWait(500, TimeUnit.MILLISECONDS))
            .withRetryListener(new RetryListener() {
                @Override
                public <V> void onRetry(Attempt<V> attempt) {
                    if (attempt.hasException()) {
                        log.warn("【重试】第 {} 次重试，异常：{}",
                                attempt.getAttemptNumber(),
                                attempt.getExceptionCause().getMessage());
                    }
                }
            })
            .build();

    /** Redis 操作重试器：最多重试 2 次，间隔 200ms */
    private static final Retryer<Boolean> REDIS_RETRYER = RetryerBuilder.<Boolean>newBuilder()
            .retryIfException()
            .withStopStrategy(StopStrategies.stopAfterAttempt(2))
            .withWaitStrategy(WaitStrategies.fixedWait(200, TimeUnit.MILLISECONDS))
            .build();

    /**
     * 带重试的数据库操作
     * @param callable 数据库操作（Callable）
     * @param errorMsg 失败日志前缀
     * @return 是否成功
     */
    public static boolean executeWithDbRetry(Callable<Boolean> callable, String errorMsg) {
        try {
            return DB_RETRYER.call(callable);
        } catch (Exception e) {
            log.error("【重试】{}，已重试3次仍然失败：{}", errorMsg, e.getMessage());
            return false;
        }
    }

    /**
     * 带重试的 Redis 操作
     * @param callable Redis 操作（Callable）
     * @param errorMsg 失败日志前缀
     * @return 是否成功
     */
    public static boolean executeWithRedisRetry(Callable<Boolean> callable, String errorMsg) {
        try {
            return REDIS_RETRYER.call(callable);
        } catch (Exception e) {
            log.error("【重试】{}，已重试2次仍然失败：{}", errorMsg, e.getMessage());
            return false;
        }
    }
}