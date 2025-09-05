package com.example.order.service;

import com.example.common.model.order.form.OrderItemForm;
import com.example.order.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    Boolean insertOrderItemList(List<OrderItem> orderItemList);
    List<OrderItem> getOrderItemList(List<Long> orderIds);
    OrderItemForm getOrderItemInfo(Long oiid);
    List<OrderItemForm> getOrderItemListByOrderIds(List<Long> orderIds);
}
