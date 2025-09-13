package com.example.common.model.product.vo;

import lombok.Data;

@Data
public class ProductInfoVO {

    private String id;
    private String name;//商品名称
    private Long stock;//库存
    private Double price;//价格
    private String category;//分类
    private String brand;//商品品牌
    private String introduce;//商品简介
    private Long visitCount;//游览总数
    private Long likeCount;//点赞人数
    private Boolean isLike;//是否点赞
    private Double score;//评分
    private Long scoreCount;//评分人数
    private Long sales;//销量
    private String cover;//封面
    private String type;
}
