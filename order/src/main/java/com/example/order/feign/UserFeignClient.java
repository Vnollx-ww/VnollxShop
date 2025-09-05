package com.example.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service",path = "/api/user") // 这里只需要服务名
public interface UserFeignClient {
    @GetMapping("/get-balance")
    Double getBalance(@RequestParam Long uid);
    @PutMapping("/update/balance")
    Boolean updateUserBalance(@RequestBody Long uid, @RequestBody Double cost);
}
