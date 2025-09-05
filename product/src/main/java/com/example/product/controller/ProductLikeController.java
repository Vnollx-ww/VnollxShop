package com.example.product.controller;

import com.example.common.exception.BusinessException;
import com.example.common.utils.NumberValidator;
import com.example.product.service.ProductLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product/like")
@RequiredArgsConstructor
public class ProductLikeController {
    private final ProductLikeService productLikeService;
    @PostMapping("/add")
    public void addLike(@RequestBody String pid, HttpServletRequest request){
        if (!NumberValidator.isInteger(pid)) {
            throw new BusinessException("无效的请求格式");
        }
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        productLikeService.addLike(uid,Long.parseLong(pid));
    }
}
