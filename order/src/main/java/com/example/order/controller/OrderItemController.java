package com.example.order.controller;

import com.example.common.model.order.form.OrderItemForm;
import com.example.common.result.Result;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/order/item")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;
    @PostMapping("/insert")
    public Result<Boolean> insertOrderItemList(@RequestBody List<OrderItem> orderItemList){
        return Result.success(orderItemService.insertOrderItemList(orderItemList));
    }
    @GetMapping("/info")
    public Result<OrderItemForm> getOrderItemInfo(@RequestParam Long oiid){
        return Result.success(orderItemService.getOrderItemInfo(oiid));
    }
    @GetMapping("/list")
    public Result<List<OrderItemForm>> getOrderItemList(@RequestParam List<Long> orderIds){
        return Result.success(orderItemService.getOrderItemListByOrderIds(orderIds));
    }
}
