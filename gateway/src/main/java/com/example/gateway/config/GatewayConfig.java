package com.example.gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .stripPrefix(1) // 移除 /auth 前缀
                                .addRequestHeader("Gateway-Request", "true")
                        )
                        .uri("lb://auth-service"))

                // 用户服务路由 - 需要认证
                .route("user-service", r -> r
                        .path("/user/**")
                        .filters(f -> f
                                .stripPrefix(1) // 移除 /user 前缀
                                .addRequestHeader("Gateway-Request", "true")
                        )
                        .uri("lb://user-service"))

                // 健康检查路由 - 直接放行
                .route("health-check", r -> r
                        .path("/actuator/**")
                        .uri("lb://gateway-service"))

                .build();
    }
}