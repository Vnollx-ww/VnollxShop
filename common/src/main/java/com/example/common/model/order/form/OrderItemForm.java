package com.example.common.model.order.form;

import lombok.Data;

@Data
public class OrderItemForm {
    private Long id;
    private Long oid;    // 关联订单ID
    private Long pid;        // 商品ID
    private String productName; //商品名称
    private Long number;     // 购买数量
    private Double cost;     // 该商品的总花费
    private Double unitPrice;// 商品单价
    private String cover;
}