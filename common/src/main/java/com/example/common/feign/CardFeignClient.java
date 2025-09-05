package com.example.common.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "card-service",path = "/api/card") // 这里只需要服务名
public interface CardFeignClient {
}
