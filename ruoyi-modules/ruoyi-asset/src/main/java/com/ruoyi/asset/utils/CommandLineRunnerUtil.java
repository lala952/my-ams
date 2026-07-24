package com.ruoyi.asset.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

public class CommandLineRunnerUtil implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandLineRunnerUtil.class);

    @Override
    public void run(String... args) throws Exception {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - freeMemory;

        log.info("系统资源信息");
        log.info("  处理器核心 : {}", availableProcessors);
        log.info("  最大堆内存 : {} MB", maxMemory / 1024 / 1024);
        log.info("  已分配内存 : {} MB", totalMemory / 1024 / 1024);
        log.info("  空闲内存   : {} MB", freeMemory / 1024 / 1024);
        log.info("  已使用内存 : {} MB", usedMemory / 1024 / 1024);
    }
}