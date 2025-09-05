package com.example.common.model.order.form;

import com.example.common.model.order.dto.CreateOrderDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderForm {
    private Long id;
    private Long uid;
    private String consignee;
    private String email;
    private String address;
    private String remark;//备注
    private Double totalCost;    // 订单总金额
    private Integer totalItems;  // 订单总商品数
    private LocalDateTime createTime;
    private List<OrderItemForm> orderItemList;
    public OrderForm(){}
    public OrderForm(CreateOrderDTO dto){

        this.uid= Long.valueOf(dto.getUid());
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