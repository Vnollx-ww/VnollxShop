package com.example.middleware.feign;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "order-service",path = "/api/order") // 这里只需要服务名
public interface OrderFeignClient {
    @PostMapping("/insert")
    Result<Void> insertOrder(@RequestBody CreateOrderDTO dto);
}
