package com.ldjt.emp.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 认证服务启动类
 * 
 * @author emp
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(
    basePackages = {"com.ldjt.emp.auth", "com.ldjt.emp.common", "com.ldjt.emp.framework"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.ldjt\\.emp\\.config\\.DataSource.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.ldjt\\.emp\\.aspect\\.DataSource.*")
    }
)
public class AuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  认证服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
