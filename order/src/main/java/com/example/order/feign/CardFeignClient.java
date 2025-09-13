package com.example.order.feign;

import com.example.common.model.cart.dto.DeleteCardItemByShopDTO;

import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "card-service",path = "/api/card") // 这里只需要服务名
public interface CardFeignClient {
    @DeleteMapping("/delete/shop")
    Result<Void> deleteCardItemByShop(@RequestBody DeleteCardItemByShopDTO dto);
}
