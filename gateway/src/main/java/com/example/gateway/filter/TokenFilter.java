package com.example.gateway.filter;

import com.example.common.result.Result;
import com.example.common.utils.JwtToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Token 认证过滤器 - Gateway 版本
 * 对请求进行token验证，支持Bearer格式的token
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 白名单路径 - 这些路径直接放行
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/login",
            "/api/user/register",
            "/actuator/health",
            "/actuator/info",
            "/actuator/gateway",
            "/actuator/metrics"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String requestPath = request.getPath().value();
        String requestMethod = request.getMethod().name();

        logger.debug("请求路径: {} {}", requestMethod, requestPath);

        // 1. 检查是否为白名单路径，直接放行
        if (isWhiteListPath(requestPath)) {
            logger.debug("白名单路径，直接放行: {} {}", requestMethod, requestPath);
            return chain.filter(exchange);
        }

        // 2. 检查是否为OPTIONS预检请求，直接放行
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            logger.debug("OPTIONS预检请求，直接放行: {}", requestPath);
            return chain.filter(exchange);
        }

        // 3. 检查请求头中的token
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (!StringUtils.hasText(authHeader)) {
            logger.warn("请求头中没有Authorization: {} {}", requestMethod, requestPath);
            return writeErrorResponse(response, "缺少Authorization头", HttpStatus.UNAUTHORIZED);
        }

        // 4. 检查Bearer格式
        if (!authHeader.startsWith("Bearer ")) {
            logger.warn("Authorization格式错误: {} {} - {}", requestMethod, requestPath, authHeader);
            return writeErrorResponse(response, "Authorization格式错误，应为Bearer token格式", HttpStatus.UNAUTHORIZED);
        }

        // 5. 提取token
        String token = authHeader.substring(7); // 去掉"Bearer "

        if (!StringUtils.hasText(token)) {
            logger.warn("token为空: {} {}", requestMethod, requestPath);
            return writeErrorResponse(response, "token不能为空", HttpStatus.UNAUTHORIZED);
        }

        try {
            // 6. 验证token
            if (!JwtToken.validateToken(token)) {
                return writeErrorResponse(response, "token无效", HttpStatus.UNAUTHORIZED);
            }

            String uid = JwtToken.getUserIdFromToken(token);
            if (uid == null) {
                logger.warn("token无效: {} {}", requestMethod, requestPath);
                return writeErrorResponse(response, "token无效", HttpStatus.UNAUTHORIZED);
            }

            logger.debug("token验证通过，用户ID: {} - {} {}", uid, requestMethod, requestPath);

            // 7. 添加用户信息到请求头，供下游服务使用
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", uid)
                    .header("X-User-Token", token)
                    .build();

            // 8. 继续执行过滤器链
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            logger.error("过滤器执行异常: {} {} - {}", requestMethod, requestPath, e.getMessage(), e);
            return writeErrorResponse(response, "服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 检查是否为白名单路径
     */
    private boolean isWhiteListPath(String requestPath) {
        return WHITE_LIST.stream().anyMatch(requestPath::startsWith);
    }

    /**
     * 写入错误响应 - WebFlux 版本
     */
    private Mono<Void> writeErrorResponse(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<Object> result = Result.error(status.value(), message);

        try {
            String jsonResponse = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            logger.error("JSON序列化错误", e);
            return Mono.error(e);
        }
    }

    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，数字越小优先级越高
        return -100;
    }
}