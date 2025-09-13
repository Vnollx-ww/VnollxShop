package com.example.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.model.product.dto.LikeUpdateDTO;
import com.example.common.model.product.dto.StockDeductDTO;
import com.example.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    void batchDeductStock(@Param("list") List<StockDeductDTO> deductList);
    void updateBatchLike(List<LikeUpdateDTO> dtoList);
    List<Product> lockProducts( List<Long> productIds);
}
