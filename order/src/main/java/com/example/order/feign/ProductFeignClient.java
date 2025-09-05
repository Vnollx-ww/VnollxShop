package com.example.order.feign;

import com.example.product.entity.Product;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service",path = "/api/mysql/product") // 这里只需要服务名
public interface ProductFeignClient {
    @GetMapping("/list")
    List<Product> getProductList(@RequestParam(required = false) List<Long> idList);
    @PutMapping("/deduct")
    Boolean deductStock(@RequestBody List<Pair<Long,Long>> deductPairs);
}
