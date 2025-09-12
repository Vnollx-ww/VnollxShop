package com.example.product.service;

import com.example.common.model.product.form.ProductForm;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.product.entity.Product;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ProductService {
    ProductInfoVO getProductInfo(Long uid,Long pid);
    List<ProductInfoVO> getProductList(List<Long> idList,String keyword,String category,String sortType);
    Boolean deductStock(List<Pair<Long,Long>> deductPairs);
    void updateLikeCount(int type,Long pid);
    List<String> getCategoryList();
}
