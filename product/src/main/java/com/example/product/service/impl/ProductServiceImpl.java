package com.example.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.exception.BusinessException;
import com.example.product.entity.Product;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductLikeService;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductLikeService productLikeService;
    @Override
    public ProductInfoVO getProductInfo( Long uid,Long pid) {
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
        vo.setId(String.valueOf(product.getId()));
        vo.setIsLike(productLikeService.judgeIsLike(uid,pid));

        return vo;
    }

    @Override
    public List<ProductInfoVO> getProductList(
            List<Long> idList,String keyword,String category,
            String sortType
    ) {
        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("id,name,stock,cover,price,introduce,category");
        if (idList!=null)queryWrapper.in("id",idList);
        if (StringUtils.isNotBlank(keyword))queryWrapper.like("name",keyword);

        if (StringUtils.isNotBlank(category))queryWrapper.eq("category",category);

        List<Product> productList = this.list(queryWrapper);

        // 使用Stream转换
        return productList.stream()
                .map(product -> {
                    ProductInfoVO vo = new ProductInfoVO();
                    BeanUtils.copyProperties(product, vo);
                    vo.setId(String.valueOf(product.getId()));
                    return vo;
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

    @Override
    public void updateLikeCount(int type,Long pid) {
        UpdateWrapper<Product> wrapper=new UpdateWrapper<>();
        wrapper.eq("id",pid).setSql("like_count = like_count + "+type);
        this.update(wrapper);
    }

    @Override
    public List<String> getCategoryList() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        // 只查询category字段，并且去重
        queryWrapper.select("DISTINCT category")
                // 可以添加条件过滤掉空值（如果需要）
                .isNotNull("category")
                .ne("category", ""); // 排除空字符串

        // 执行查询，获取分类列表
        List<Product> products = this.list(queryWrapper);

        // 转换为字符串列表
        return products.stream()
                .map(Product::getCategory) // 提取category字段
                .collect(Collectors.toList());
    }

}
