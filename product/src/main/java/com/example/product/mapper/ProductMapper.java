package com.example.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.product.entity.Product;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    int batchDeductStock(@Param("list") List<Pair<Long, Long>> deductPairs);
    List<Product> lockProducts( List<Long> productIds);
}
