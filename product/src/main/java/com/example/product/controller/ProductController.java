package com.example.product.controller;

import com.example.common.exception.BusinessException;
import com.example.common.model.product.form.ProductForm;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.result.Result;
import com.example.common.utils.NumberValidator;
import com.example.product.entity.Product;
import com.example.product.service.ProductLikeService;
import com.example.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping("/info")
    public ProductInfoVO getProductInfo(@RequestParam String pid, HttpServletRequest request){
        if (!NumberValidator.isInteger(pid)){
            throw new BusinessException("无效的请求格式");
        }
        return productService.getProductInfo(Long.parseLong(pid));
    }
    @GetMapping("/list")
    public List<ProductForm> getProductList(@RequestParam(required = false) List<Long> idList){
        return productService.getProductList(idList);
    }
    @PutMapping("/deduct")
    public Boolean deductStock(@RequestBody List<Pair<Long,Long>> deductPairs){
        return productService.deductStock(deductPairs);
    }
}
