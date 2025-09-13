package com.example.common.model.cart.dto;

import lombok.Data;

@Data
public class AddCardItemDTO {
    private String productName;
    private String pid;
    private String cover;
    private Double unitPrice;
}
