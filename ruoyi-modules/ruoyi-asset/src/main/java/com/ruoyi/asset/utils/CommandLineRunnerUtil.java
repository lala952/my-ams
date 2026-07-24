package com.ruoyi.asset.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerUtil implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CommandLineRunnerUtil.class);
    @Override
    public void run(String... args) throws Exception {
        log.info("当前CPU核数（source code: Runtime.getRuntime().availableProcessors()）：{}",Runtime.getRuntime().availableProcessors());
    }
}
