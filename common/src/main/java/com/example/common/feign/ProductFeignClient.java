package com.example.common.feign;

import com.example.common.exception.BusinessException;
import com.example.common.model.product.form.ProductForm;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.utils.NumberValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service",path = "/api/product") // 这里只需要服务名
public interface ProductFeignClient {
    @GetMapping("/info")
    ProductInfoVO getProductInfo(@RequestParam String pid, HttpServletRequest request);
    @GetMapping("/list")
    List<ProductForm> getProductList(@RequestParam(required = false) List<Long> idList);
    @PutMapping("/deduct")
    Boolean deductStock(@RequestBody List<Pair<Long,Long>> deductPairs);
    @PostMapping("/like/add")
    void addLike(@RequestBody String pid, HttpServletRequest request);
}
