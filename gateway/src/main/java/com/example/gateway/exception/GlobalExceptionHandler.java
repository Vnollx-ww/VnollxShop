package com.example.gateway.exception;

import com.example.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局异常处理器
 * 处理gateway中的各种异常情况
 */
@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // 设置响应状态码
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 构建错误响应
        String errorMessage = "网关服务异常: " + ex.getMessage();
        Result<Object> result = Result.error(500, errorMessage);
        
        // 记录错误日志
        logger.error("网关异常: {} - {}", exchange.getRequest().getPath(), ex.getMessage(), ex);
        
        // 写入响应
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonResponse = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            logger.error("序列化响应失败", e);
            String fallbackResponse = "{\"code\":500,\"message\":\"网关服务异常\"}";
            DataBuffer buffer = response.bufferFactory().wrap(fallbackResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}
