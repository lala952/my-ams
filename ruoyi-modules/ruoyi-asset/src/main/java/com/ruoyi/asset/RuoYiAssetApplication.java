package com.ruoyi.asset;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCustomConfig
@EnableRyFeignClients
@EnableFeignClients(basePackages = {"com.ruoyi.api", "com.ruoyi.workflow", "com.ruoyi.system.api"})
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.ruoyi.asset", "com.ruoyi.system.api", "com.ruoyi.workflow.api"})
public class RuoYiAssetApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiAssetApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  嘻嘻 RuoYiAssetApplication模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}