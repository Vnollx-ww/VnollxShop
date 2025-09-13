package com.example.product.service;

import com.example.common.model.product.dto.LikeUpdateDTO;
import com.example.common.model.product.dto.StockDeductDTO;
import com.example.common.model.product.vo.ProductInfoVO;

import java.util.List;

public interface ProductService {
    ProductInfoVO getProductInfo(Long uid,Long pid);
    List<ProductInfoVO> getProductList(List<Long> idList,String keyword,String category,String sortType,String type);
    void deductStock(List<StockDeductDTO> deductList);
    void updateLikeCount(int type,Long pid);
    void updateBatchLike(List<LikeUpdateDTO> dtoList);
    List<String> getCategoryList();
}
