package com.example.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CardItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uid;
    private Long pid;
    private Long number;
    private String productName;
    private String cover;
    private Double unitPrice;
}
