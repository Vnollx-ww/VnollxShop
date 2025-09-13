package com.example.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.order.dto.CreateOrderDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uid;
    private String consignee;
    private String email;
    private String address;
    private String remark;//备注
    private Double totalCost;    // 订单总金额
    private Integer totalItems;  // 订单总商品数
    private LocalDateTime createTime;
    @TableField(exist = false)
    private List<OrderItem> orderItemList;
    public Order(){}
    public Order(CreateOrderDTO dto,Long uid){

        this.uid= uid;
        this.consignee=dto.getConsignee();
        this.email=dto.getEmail();
        this.address=dto.getAddress();
        this.remark=dto.getRemark();
        this.totalCost= dto.getItems().stream()
                .mapToDouble(item -> item.getNumber() * item.getUnitPrice())
                .sum();
        this.totalItems=dto.getItems().size();
    }
}