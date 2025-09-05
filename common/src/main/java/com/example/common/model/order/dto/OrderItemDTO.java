package com.example.common.model.order.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private String pid;

    private Long number;

    private Double unitPrice;

    private String productName;
}
