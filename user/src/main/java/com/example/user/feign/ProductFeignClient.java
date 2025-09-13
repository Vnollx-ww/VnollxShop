package com.example.user.feign;

import com.example.common.model.product.dto.StockDeductDTO;
import com.example.common.model.product.form.ProductForm;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service",path = "/api/product") // 这里只需要服务名
public interface ProductFeignClient {
    @GetMapping("/info")
    Result<ProductInfoVO> getProductInfo(@RequestParam String pid);
    @GetMapping("/list")
    Result<List<ProductForm>> getProductList(@RequestParam(required = false) List<Long> idList);
    @PutMapping("/deduct")
    Result<Void> deductStock(@RequestBody List<StockDeductDTO> deductList);
    @PostMapping("/like/add")
    Result<Void> addLike(@RequestBody String pid);
}
