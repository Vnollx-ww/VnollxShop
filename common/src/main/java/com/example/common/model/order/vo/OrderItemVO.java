package com.example.common.model.order.vo;

import com.example.common.model.order.form.OrderItemForm;
import lombok.Data;

@Data
public class OrderItemVO {
    private Long id;
    private Long oid;    // 关联订单ID
    private Long pid;        // 商品ID
    private String productName; //商品名称
    private Long number;     // 购买数量
    private Double cost;     // 该商品的总花费
    private Double unitPrice;// 商品单价
    public OrderItemVO(){}
    public OrderItemVO(OrderItemForm orderItemForm){
        this.id=orderItemForm.getId();
        this.oid=orderItemForm.getOid();
        this.cost=orderItemForm.getCost();
        this.pid=orderItemForm.getPid();
        this.productName=orderItemForm.getProductName();
        this.number=orderItemForm.getNumber();
        this.unitPrice=orderItemForm.getUnitPrice();
    }
}
