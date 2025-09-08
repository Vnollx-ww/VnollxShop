package com.example.product.service;

import com.example.common.model.product.form.ProductForm;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.product.entity.Product;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ProductService {
    ProductInfoVO getProductInfo(Long uid,Long pid);
    List<ProductForm> getProductList(List<Long> idList);
    Boolean deductStock(List<Pair<Long,Long>> deductPairs);
}
