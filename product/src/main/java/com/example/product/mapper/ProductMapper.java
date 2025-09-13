package com.example.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.model.product.dto.StockDeductDTO;
import com.example.product.entity.Product;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    int batchDeductStock(@Param("list") List<StockDeductDTO> deductList);
    List<Product> lockProducts( List<Long> productIds);
}
