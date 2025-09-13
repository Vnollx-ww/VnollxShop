package com.example.product.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.common.utils.NumberValidator;
import com.example.product.service.ProductLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/like")
@RequiredArgsConstructor
public class ProductLikeController {
    private final ProductLikeService productLikeService;
    @PostMapping("/add")
    public Result<Void> addLike(@RequestParam String pid, HttpServletRequest request){
        if (!NumberValidator.isInteger(pid)) {
            throw new BusinessException("无效的请求格式");
        }
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        productLikeService.addLike(uid,Long.parseLong(pid));
        return Result.success();
    }
    @PostMapping("/cancel")
    public Result<Void> cancelLike(@RequestParam String pid, HttpServletRequest request){
        if (!NumberValidator.isInteger(pid)) {
            throw new BusinessException("无效的请求格式");
        }
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        productLikeService.cancelLike(uid,Long.parseLong(pid));
        return Result.success();
    }
}
