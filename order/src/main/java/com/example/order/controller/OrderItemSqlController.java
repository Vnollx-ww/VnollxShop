package com.example.order.controller;

import com.example.common.model.order.form.OrderItemForm;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/order/item")
@RequiredArgsConstructor
public class OrderItemSqlController {
    private final OrderItemService orderItemService;
    @PostMapping("/insert")
    public Boolean insertOrderItemList(@RequestBody List<OrderItem> orderItemList){return orderItemService.insertOrderItemList(orderItemList);}
    @GetMapping("/info")
    public OrderItemForm getOrderItemInfo(@RequestParam Long oiid){
        return orderItemService.getOrderItemInfo(oiid);
    }
    @GetMapping("/list")
    public List<OrderItemForm> getOrderItemList(@RequestParam List<Long> orderIds){
        return orderItemService.getOrderItemListByOrderIds(orderIds);
    }
}
