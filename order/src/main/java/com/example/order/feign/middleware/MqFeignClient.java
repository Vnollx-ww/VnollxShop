package com.example.order.feign.middleware;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "middleware-service",path = "/api/middleware/mq") // 这里只需要服务名
public interface MqFeignClient {
    @PostMapping("/order")
    Result<Void> createOrderMessage(@RequestBody CreateOrderDTO dto);
}
