package com.example.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.model.product.form.ProductForm;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.exception.BusinessException;
import com.example.product.entity.Product;
import com.example.product.feign.UserFeignClient;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductLikeService;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements ProductService {
    private final UserFeignClient userFeignClient;
    private final ProductLikeService productLikeService;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Override
    public ProductInfoVO getProductInfo( Long pid) {
        Product product=this.getById(pid);
        if (product==null){
            throw new BusinessException("商品不存在");
        }
        UpdateWrapper<Product> wrapper=new UpdateWrapper<>();
        wrapper.eq("id",pid).setSql("visit_count = visit_count + 1");
        int row=this.baseMapper.update(null, wrapper);
        if (row==0){
            logger.error("更新商品游览量数失败");
        }
        ProductInfoVO vo=new ProductInfoVO();
        BeanUtils.copyProperties(product,vo);
        return vo;
    }

    @Override
    public List<ProductForm> getProductList(List<Long> idList) {
        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("id,name,stock");
        if (idList!=null)queryWrapper.in("id",idList);
        List<Product> productList = this.list(queryWrapper);

        // 使用Stream转换
        return productList.stream()
                .map(product -> {
                    ProductForm form = new ProductForm();
                    BeanUtils.copyProperties(product, form);
                    return form;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean deductStock(List<Pair<Long, Long>> deductPairs) {

        if (CollectionUtils.isEmpty(deductPairs)) {
            return true;
        }
        List<Long> productIds = deductPairs.stream()
                .map(Pair::getKey)
                .sorted()
                .collect(Collectors.toList());

        this.baseMapper.lockProducts(productIds);//加锁

        int affectedRows = baseMapper.batchDeductStock(deductPairs);

        return affectedRows == deductPairs.size();
    }

}
