package com.example.order.service;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.common.model.order.dto.DeleteOrderDTO;
import com.example.common.model.order.vo.OrderItemVO;
import com.example.common.model.order.vo.OrderVO;
import com.example.order.entity.OrderItem;

import java.util.List;

public interface OrderService {
    void createOrder(CreateOrderDTO dto,Long uid);
    void insertOrder(CreateOrderDTO dto);
    void deleteOrder(DeleteOrderDTO dto);
    List<OrderVO> getOrderList(Long uid);
}
