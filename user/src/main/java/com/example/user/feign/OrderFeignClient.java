package com.example.user.feign;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.model.order.dto.DeleteOrderDTO;
import com.example.common.model.order.form.OrderItemForm;
import com.example.common.model.order.vo.OrderVO;
import com.example.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service",path = "/api/order") // 这里只需要服务名
public interface OrderFeignClient {
    @PostMapping("/create")
    Result<Void> createOrder(@ModelAttribute CreateOrderDTO dto);
    @DeleteMapping("/delete")
    Result<Void> deleteOrder(@ModelAttribute DeleteOrderDTO dto);
    @GetMapping("/list")
    Result<List<OrderVO>> getOrderList(HttpServletRequest request);
    @PostMapping("/item/insert")
    Result<Boolean> insertOrderItemList(@RequestBody List<OrderItemForm> orderItemList);
    @GetMapping("/item/info")
    Result<OrderItemForm> getOrderItemInfo(@RequestParam Long oiid);
    @GetMapping("/item/list")
    Result<List<OrderItemForm>> getOrderItemList(@RequestParam List<Long> orderIds);
}
