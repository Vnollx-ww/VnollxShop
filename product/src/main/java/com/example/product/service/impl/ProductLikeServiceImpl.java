package com.example.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.BusinessException;
import com.example.product.entity.ProductLike;
import com.example.product.mapper.ProductLikeMapper;
import com.example.product.service.ProductLikeService;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeServiceImpl extends ServiceImpl<ProductLikeMapper, ProductLike> implements ProductLikeService {
    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void addLike(Long uid, Long pid) {
        productService.updateLikeCount(1,pid);
        QueryWrapper<ProductLike> wrapper=new QueryWrapper<>();
        wrapper.eq("pid",pid).eq("uid",uid);
        if (this.count(wrapper)>0){
            throw new BusinessException("您已经点赞过该商品了");
        }
        ProductLike productLike=new ProductLike();
        productLike.setPid(pid);
        productLike.setUid(uid);
        this.save(productLike);
    }

    @Override
    public Boolean judgeIsLike(Long uid, Long pid) {
        QueryWrapper<ProductLike> wrapper=new QueryWrapper<>();
        wrapper.eq("pid",pid).eq("uid",uid);
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional
    public void cancelLike(Long uid, Long pid) {
        productService.updateLikeCount(-1,pid);
        QueryWrapper<ProductLike> wrapper=new QueryWrapper<>();
        wrapper.eq("uid",uid).eq("pid",pid);
        ProductLike productLike=this.getOne(wrapper);
        if(productLike==null){
            throw new BusinessException("您尚未点赞该商品");
        }
        this.baseMapper.deleteById(productLike);
    }
}
