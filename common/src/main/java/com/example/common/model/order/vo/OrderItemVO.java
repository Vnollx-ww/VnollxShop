package com.example.common.model.order.vo;

import com.example.common.model.order.form.OrderItemForm;
import lombok.Data;

@Data
public class OrderItemVO {
    private String id;
    private String oid;    // 关联订单ID
    private String pid;        // 商品ID
    private String productName; //商品名称
    private Long number;     // 购买数量
    private Double cost;     // 该商品的总花费
    private Double unitPrice;// 商品单价
    private String cover;
    public OrderItemVO(){}
    public OrderItemVO(OrderItemForm orderItemForm){
        this.id=String.valueOf(orderItemForm.getId());
        this.oid=String.valueOf(orderItemForm.getOid());
        this.cost=orderItemForm.getCost();
        this.pid=String.valueOf(orderItemForm.getPid());
        this.productName=orderItemForm.getProductName();
        this.number=orderItemForm.getNumber();
        this.unitPrice=orderItemForm.getUnitPrice();
        this.cover=orderItemForm.getCover();
    }
}
