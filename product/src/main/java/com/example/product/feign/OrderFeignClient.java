package com.example.product.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order-service",path = "/api/order") // 这里只需要服务名
public interface OrderFeignClient {

}
