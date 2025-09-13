package com.example.common.model.order.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class CreateOrderDTO {
    private List<OrderItemDTO> items;
    private Double totalCost;
    private String consignee;     // 收货人
    private String email;         // 联系邮箱
    private String address;       // 详细地址

    // 可选字段：支付信息
    private String remark;        // 订单备注
    private String type;
}
