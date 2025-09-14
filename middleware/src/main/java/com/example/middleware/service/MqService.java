package com.example.middleware.service;

import com.example.common.model.order.dto.CreateOrderDTO;

public interface MqService {
    void createOrderMessage(CreateOrderDTO dto);
}
