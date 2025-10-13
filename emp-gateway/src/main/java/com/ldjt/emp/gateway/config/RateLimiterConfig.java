package com.ldjt.emp.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 限流配置
 * 
 * @author emp
 */
@Configuration
public class RateLimiterConfig {
    
    /**
     * 基于IP的限流
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(hostAddress);
        };
    }
    
    /**
     * 基于用户的限流
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("userId");
            return Mono.just(userId == null ? "anonymous" : userId);
        };
    }
    
    /**
     * 基于接口的限流
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            return Mono.just(path);
        };
    }
}
