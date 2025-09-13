package com.example.product.controller;

import com.example.common.exception.BusinessException;
import com.example.common.model.product.dto.LikeUpdateDTO;
import com.example.common.model.product.dto.StockDeductDTO;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.result.Result;
import com.example.common.utils.NumberValidator;
import com.example.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping("/info")
    public Result<ProductInfoVO> getProductInfo(@RequestParam String pid, HttpServletRequest request){
        if (!NumberValidator.isInteger(pid)){
            throw new BusinessException("无效的请求格式");
        }
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        ProductInfoVO productInfo = productService.getProductInfo(uid, Long.parseLong(pid));
        return Result.success(productInfo);
    }

    @GetMapping("/list")
    public Result<List<ProductInfoVO>> getProductList(
            @RequestParam(required = false) List<Long> idList,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortType,
            @RequestParam(required = false) String type
    ){
        List<ProductInfoVO> productList = productService.getProductList(idList, keyword, category, sortType,type);
        return Result.success(productList);
    }

    @GetMapping("/list/category")
    public Result<List<String>> getCategoryList(){
        List<String> categoryList = productService.getCategoryList();
        return Result.success(categoryList);
    }

    @PutMapping("/deduct")
    public Result<Void> deductStock(@RequestBody  List<StockDeductDTO> deductList){
        productService.deductStock(deductList);
        return Result.success();
    }
    @PutMapping("/update/like")
    public Result<Void> updateBatchLike(@RequestBody List<LikeUpdateDTO> dtoList){
        productService.updateBatchLike(dtoList);
        return Result.success();
    }
}
