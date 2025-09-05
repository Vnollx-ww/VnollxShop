package com.example.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.product.entity.ProductLike;
import com.example.product.mapper.ProductLikeMapper;
import com.example.product.service.ProductLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeServiceImpl extends ServiceImpl<ProductLikeMapper, ProductLike> implements ProductLikeService {
    @Override
    @Transactional
    public Boolean addLike(Long uid, Long pid) {
        UpdateWrapper<ProductLike> wrapper=new UpdateWrapper<>();
        wrapper.eq("pid",pid);
        int row=this.baseMapper.update(null, wrapper);
        if (row==0)return false;
        ProductLike productLike=new ProductLike();
        productLike.setPid(pid);
        productLike.setUid(uid);
        return this.save(productLike);
    }
}
