package com.ruoyi.asset.config;

import com.ruoyi.asset.constant.ThreadPoolExecutorConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

/**
 * 线程池初始化配置
 * 【修改点1】应用启动时将 Spring 管理的线程池注入到常量类
 */
@Configuration
public class ThreadPoolInitConfig {

    @Resource
    @Qualifier("bizExecutor")
    private ExecutorService bizExecutor;

    @Resource
    @Qualifier("ioExecutor")
    private ExecutorService ioExecutor;

    @PostConstruct
    public void init() {
        ThreadPoolExecutorConstants.BIZ_EXECUTOR = bizExecutor;
        ThreadPoolExecutorConstants.IO_EXECUTOR = ioExecutor;
    }
}