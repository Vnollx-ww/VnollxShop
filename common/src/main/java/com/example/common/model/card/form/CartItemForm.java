package com.example.common.model.card.form;

import lombok.Data;

@Data
public class CartItemForm {
    private Long id;
    private Long uid;
    private Long pid;
    private Long number;
    private String productName;
    private String cover;
    private Double unitPrice;
}
