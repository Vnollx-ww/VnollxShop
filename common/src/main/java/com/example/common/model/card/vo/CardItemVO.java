package com.example.common.model.card.vo;

import lombok.Data;

@Data
public class CardItemVO {
    private String id;
    private String pid;
    private Long number;
    private String productName;
    private String cover;
    private Double unitPrice;
}
