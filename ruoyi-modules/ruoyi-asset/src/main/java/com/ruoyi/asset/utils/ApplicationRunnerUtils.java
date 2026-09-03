package com.ruoyi.asset.utils;

import com.ruoyi.common.log.annotation.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author xiaowang
 * @date 2026-09-03
 */
public class ApplicationRunnerUtils implements ApplicationRunner {
   private static final Logger log = LoggerFactory.getLogger(ApplicationRunnerUtils.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        /**
         * ApplicationRunner 和 CommandLineRunner 一样，可以在 SpringBoot 项目启动之前进行一些操作
         * 同样是实现接口，两个接口都只提供了一个 run() 方法
         */
    }


}
