package com.ruoyi.asset.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiaowang
 * @date 2026-09-03
 */
public class CommandLineRunnerUtils implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandLineRunnerUtils.class);
    /**
     *  CommandLineRunner 和 ApplicationRunner 一样，可以在 SpringBoot 项目启动之前进行一些操作
     * 同样是实现接口，两个接口都只提供了一个 run() 方法
     */
    @Override
    public void run(String... args) throws Exception {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - freeMemory;

        log.info("系统资源信息");
        log.info("  处理器核心/CPU线程数:{}", availableProcessors);
        log.info("  最大堆内存/JVM能从系统中获取总内存大小（单位：byte）:{} B，{} KB，{} MB，{} GB", maxMemory, maxMemory / 1024, maxMemory / 1024 / 1024, maxMemory / 1024 / 1024 / 1024);
        log.info("  已分配内存/JVM已经从系统中获取总内存大小（单位：byte）:{} MB", totalMemory / 1024 / 1024);
        log.info("  空闲内存/JVM剩余内存大小（单位：byte）:{} MB", freeMemory / 1024 / 1024);
        log.info("  已使用内存:{} MB", usedMemory / 1024 / 1024);

        AtomicInteger atomicInteger = new AtomicInteger();
        ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("availableProcessors", availableProcessors);
        hashMap.put("freeMemory", freeMemory / 1024 / 1024);
        hashMap.put("maxMemory", maxMemory / 1024 / 1024);
        hashMap.put("totalMemory", totalMemory / 1024 / 1024);
        hashMap.put("usedMemory", usedMemory / 1024 / 1024);
        System.out.println("属性 + 值");
        hashMap.forEach((k, v) -> System.out.println(k + ":\t" + v + "MB"));
    }

    public static void main(String[] args) throws Exception {
        new CommandLineRunnerUtils().run();
    }
}