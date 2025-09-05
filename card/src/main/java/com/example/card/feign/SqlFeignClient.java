package com.example.card.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "data-service",path = "/api/mysql") // 这里只需要服务名
public interface SqlFeignClient {

}
