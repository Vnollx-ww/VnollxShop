package com.example.product.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.api.product.ProductService;
import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}
