package com.example.user.feign;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.model.order.dto.DeleteOrderDTO;
import com.example.common.model.order.form.OrderItemForm;
import com.example.common.model.order.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service",path = "/api/order") // 这里只需要服务名
public interface OrderFeignClient {
    @PostMapping("/create")
    void createOrder(@ModelAttribute CreateOrderDTO dto);
    @DeleteMapping("/delete")
    void deleteOrder(@ModelAttribute DeleteOrderDTO dto);
    @GetMapping("/list")
    List<OrderVO> getOrderList(HttpServletRequest request);
    @PostMapping("/item/insert")
    Boolean insertOrderItemList(@RequestBody List<OrderItemForm> orderItemList);
    @GetMapping("/item/info")
    OrderItemForm getOrderItemInfo(@RequestParam Long oiid);
    @GetMapping("/item/list")
    List<OrderItemForm> getOrderItemList(@RequestParam List<Long> orderIds);
}
