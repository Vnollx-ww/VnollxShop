package com.example.common.model.order.vo;

import com.example.common.model.order.form.OrderForm;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String consignee;
    private String email;
    private String address;
    private String remark;//备注
    private Double totalCost;    // 订单总金额
    private Integer totalItems;  // 订单总商品数
    private LocalDateTime createTime;
    private List<OrderItemVO> orderItemVOList;
    public OrderVO(){}
    public OrderVO(OrderForm orderForm){
        this.id=orderForm.getId();
        this.consignee=orderForm.getConsignee();
        this.email=orderForm.getEmail();
        this.address=orderForm.getAddress();
        this.remark=orderForm.getRemark();
        this.totalCost= orderForm.getTotalCost();
        this.totalItems=orderForm.getTotalItems();
        this.createTime=orderForm.getCreateTime();
    }
}
