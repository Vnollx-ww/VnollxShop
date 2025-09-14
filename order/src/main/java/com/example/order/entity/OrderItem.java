package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.order.dto.OrderItemDTO;
import lombok.Data;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long oid;    // 关联订单ID
    private Long pid;        // 商品ID
    private String productName; //商品名称
    private Long number;     // 购买数量
    private Double cost;     // 该商品的总花费
    private Double unitPrice;// 商品单价
    private String cover; //商品封面
    public OrderItem(){}
    public OrderItem(OrderItemDTO dto, Long oid){
        this.cost=dto.getUnitPrice()*dto.getNumber();
        this.oid=oid;
        this.pid= Long.valueOf(dto.getPid());
        this.number=dto.getNumber();
        this.productName=dto.getProductName();
        this.unitPrice=dto.getUnitPrice();
        this.cover=dto.getCover();
    }
}