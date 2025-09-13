package com.example.common.model.cart.vo;

import lombok.Data;

@Data
public class CartItemVO {
    private String id;
    private String pid;
    private Long number;
    private String productName;
    private String cover;
    private Double unitPrice;
}
