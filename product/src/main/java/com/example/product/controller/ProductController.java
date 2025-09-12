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
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        return productService.getProductInfo(uid,Long.parseLong(pid));
    }
    @GetMapping("/list")
    public List<ProductInfoVO> getProductList(
            @RequestParam(required = false) List<Long> idList,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortType
    ){
        return productService.getProductList(idList,keyword,category,sortType);
    }
    @GetMapping("/list/category")
    public List<String> getCategoryList(){
        return productService.getCategoryList();
    }
    @PutMapping("/deduct")
    public Boolean deductStock(@RequestBody List<Pair<Long,Long>> deductPairs){
        return productService.deductStock(deductPairs);
    }
}
