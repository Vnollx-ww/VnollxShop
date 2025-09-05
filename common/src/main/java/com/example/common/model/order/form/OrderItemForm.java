package com.example.common.model.order.form;

import com.example.common.model.order.dto.OrderItemDTO;
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
    public OrderItemForm(){}
    public OrderItemForm(OrderItemDTO dto, Long oid){
        this.cost=dto.getUnitPrice()*dto.getNumber();
        this.oid=oid;
        this.pid= Long.valueOf(dto.getPid());
        this.number=dto.getNumber();
        this.productName=dto.getProductName();
        this.unitPrice=dto.getUnitPrice();
    }
}