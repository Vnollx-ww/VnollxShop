package com.example.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("product_like")
public class ProductLike {
    private Long pid;
    private Long uid;
    @TableId(type = IdType.AUTO)
    private Long id;
}
