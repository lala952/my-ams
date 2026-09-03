package com.ruoyi.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Activiti 配置类
 * 解决 Activiti 需要 UserDetailsService 的问题
 *
 * @author ruoyi
 * @date 2026-09-03
 */
@Configuration
public class ActivitiConfig {

    /**
     * 提供一个空的 UserDetailsService，满足 Activiti 的依赖需求
     * 实际用户认证使用 Ruoyi 自己的 Security 框架
     *
     * @return 空的用户详情服务实例
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }
}
