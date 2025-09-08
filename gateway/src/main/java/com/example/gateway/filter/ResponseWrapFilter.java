package com.example.gateway.filter;

import com.example.common.result.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局响应包装过滤器
 * 统一包装所有2xx成功的响应为Result格式
 */
@Slf4j
@Component
public class ResponseWrapFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public @NotNull Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                HttpStatusCode status = getStatusCode();

                log.debug("处理响应，状态码: {}, 路径: {}", status, exchange.getRequest().getPath());

                // 只处理2xx成功的响应
                if (status == null || !status.is2xxSuccessful()) {
                    log.debug("非2xx响应，跳过包装");
                    return super.writeWith(body);
                }

                // 处理不同类型的响应体
                if (body instanceof Flux<? extends DataBuffer>) {
                    Flux<? extends DataBuffer> flux = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(flux.buffer().map(dataBuffers -> {
                        // 合并所有数据缓冲区
                        byte[] combinedContent = combineDataBuffers(dataBuffers);
                        String originalBody = new String(combinedContent, StandardCharsets.UTF_8);

                        log.debug("原始响应体: {}", originalBody);

                        // 处理响应包装
                        byte[] wrappedContent = wrapResponse(originalBody);

                        // 更新响应头
                        getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        getHeaders().setContentLength(wrappedContent.length);

                        log.debug("包装后响应体: {}", new String(wrappedContent, StandardCharsets.UTF_8));

                        return bufferFactory.wrap(wrappedContent);
                    }));
                } else if (body instanceof Mono<? extends DataBuffer>) {
                    Mono<? extends DataBuffer> mono = (Mono<? extends DataBuffer>) body;
                    return super.writeWith(mono.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);

                        String originalBody = new String(content, StandardCharsets.UTF_8);
                        log.debug("原始响应体: {}", originalBody);

                        // 处理响应包装
                        byte[] wrappedContent = wrapResponse(originalBody);

                        // 更新响应头
                        getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        getHeaders().setContentLength(wrappedContent.length);

                        log.debug("包装后响应体: {}", new String(wrappedContent, StandardCharsets.UTF_8));

                        return bufferFactory.wrap(wrappedContent);
                    }));
                }

                log.debug("未知响应体类型，直接返回");
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 合并多个DataBuffer
     */
    private byte[] combineDataBuffers(java.util.List<? extends DataBuffer> dataBuffers) {
        int totalSize = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
        byte[] combinedContent = new byte[totalSize];
        int offset = 0;

        for (DataBuffer dataBuffer : dataBuffers) {
            int length = dataBuffer.readableByteCount();
            dataBuffer.read(combinedContent, offset, length);
            offset += length;
            DataBufferUtils.release(dataBuffer);
        }

        return combinedContent;
    }

    /**
     * 包装响应内容
     */
    private byte[] wrapResponse(String originalBody) {
        try {
            // 检查是否已经是Result格式
            if (isAlreadyResultFormat(originalBody)) {
                log.debug("已经是Result格式，不需要包装");
                return originalBody.getBytes(StandardCharsets.UTF_8);
            }

            // 解析原始响应
            Object data = parseOriginalResponse(originalBody);

            // 包装为Result格式
            Result<Object> result = Result.success();
            result.setData(data);

            String wrappedJson = objectMapper.writeValueAsString(result);
            return wrappedJson.getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("包装响应失败", e);
            // 包装失败，返回原始内容
            return originalBody.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * 解析原始响应
     */
    private Object parseOriginalResponse(String originalBody) {
        if (originalBody == null || originalBody.trim().isEmpty()) {
            return null;
        }

        try {
            // 尝试解析为JSON
            return objectMapper.readValue(originalBody, Object.class);
        } catch (JsonProcessingException e) {
            log.debug("无法解析为JSON，作为字符串处理: {}", originalBody);
            // 如果不是有效的JSON，返回原始字符串
            return originalBody;
        }
    }

    /**
     * 检查响应是否已经是Result格式
     * 更严格的检查方式
     */
    private boolean isAlreadyResultFormat(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            // 检查是否包含Result的必要字段
            boolean hasCode = jsonNode.has("code");
            boolean hasMessage = jsonNode.has("message") || jsonNode.has("msg");
            boolean hasData = jsonNode.has("data");

            boolean isResult = hasCode && hasMessage;
            log.debug("Result格式检查: hasCode={}, hasMessage={}, hasData={}, isResult={}",
                    hasCode, hasMessage, hasData, isResult);

            return isResult;

        } catch (JsonProcessingException e) {
            log.debug("JSON解析失败，不是Result格式: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public int getOrder() {
        // 提高优先级，确保在其他过滤器之前执行
        return -100;
    }
}