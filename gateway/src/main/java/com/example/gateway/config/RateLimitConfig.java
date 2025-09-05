package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 限流配置类
 */
@Configuration
public class RateLimitConfig {

    /**
     * Redis限流器配置
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // 默认限流配置：每秒10个请求，最大突发20个请求
        return new RedisRateLimiter(10, 20);
    }

    /*
      用户限流键解析器
      优先使用用户ID，如果没有则使用IP地址
     */
    /*
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 优先从请求头获取用户ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isEmpty()) {
                return Mono.just("user:" + userId);
            }
            
            // 如果没有用户ID，则使用IP地址
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just("ip:" + ip);
        };
    }
    */

    /**
     * IP限流键解析器
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
            return Mono.just("ip:" + ip);
        };
    }
}

