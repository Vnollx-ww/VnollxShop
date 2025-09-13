package com.example.order.controller;

import com.example.common.exception.BusinessException;
import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.model.order.dto.DeleteOrderDTO;
import com.example.common.model.order.vo.OrderItemVO;
import com.example.common.model.order.vo.OrderVO;
import com.example.common.result.Result;
import com.example.common.utils.NumberValidator;
import com.example.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/create")
    public Result<Void> createOrder(@ModelAttribute CreateOrderDTO dto,HttpServletRequest request){
        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        orderService.createOrder(dto,uid);
        return Result.success();
    }
    @DeleteMapping("/delete")
    public Result<Void> deleteOrder(@ModelAttribute DeleteOrderDTO dto){
        if (!NumberValidator.isInteger(dto.getOid())){
            throw new BusinessException("无效的请求格式");
        }
        orderService.deleteOrder(dto);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList(HttpServletRequest request){

        Long uid = Long.parseLong(request.getHeader("X-User-Id"));
        return Result.success(orderService.getOrderList(uid));
    }
}
