package com.example.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.model.product.dto.LikeUpdateDTO;
import com.example.common.model.product.dto.StockDeductDTO;
import com.example.common.model.product.vo.ProductInfoVO;
import com.example.common.exception.BusinessException;
import com.example.product.entity.Product;
import com.example.product.feign.middleware.RedisFeignClient;
import com.example.product.filter.BloomFilter;
import com.example.product.mapper.ProductMapper;
import com.example.product.service.ProductLikeService;
import com.example.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductLikeService productLikeService;
    @Autowired
    private BloomFilter bloomFilter;
    private final RedisFeignClient redisFeignClient;
    @Override
    public ProductInfoVO getProductInfo( Long uid,Long pid) {
        //布隆过滤器筛选
        if (!bloomFilter.contains(String.valueOf(pid))){
            logger.info("商品不存在，已由布隆过滤器筛选掉");
            return null;
        }
        String key="product:"+pid;
        String jsonProductInfo= (String) redisFeignClient.getValue(key).getData();
        if (jsonProductInfo!=null){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ProductInfoVO vo = objectMapper.readValue(jsonProductInfo, ProductInfoVO.class);
                vo.setIsLike(productLikeService.judgeIsLike(uid,pid));
                Integer likeCount= (Integer) redisFeignClient.getValue("like:"+pid).getData();
                if (likeCount!=null) vo.setLikeCount(Long.valueOf(likeCount));
                return vo;
            } catch (JsonProcessingException e) {
                logger.error("JSON反序列化失败: {}", e.getMessage());
            }
        }
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
        Integer likeCount= (Integer) redisFeignClient.getValue("like:"+pid).getData();
        if (likeCount!=null) vo.setLikeCount(Long.valueOf(likeCount));
        return vo;
    }

    @Override
    public List<ProductInfoVO> getProductList(
            List<Long> idList,String keyword,String category,
            String sortType,String type
    ) {
        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.select("id,name,stock,cover,price,introduce,category,type");
        if (idList!=null)queryWrapper.in("id",idList);
        if (StringUtils.isNotBlank(keyword))queryWrapper.like("name",keyword);

        if (StringUtils.isNotBlank(category))queryWrapper.eq("category",category);

        if (StringUtils.isNotBlank(type)){
            queryWrapper.eq("type",type);
        }
        else {
            queryWrapper.eq("type",'0');
        }
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
    public void deductStock(List<StockDeductDTO> deductList) {
        if (CollectionUtils.isEmpty(deductList)) {
            return ;
        }

        List<Long> productIds = deductList.stream()
                .map(StockDeductDTO::getProductId)
                .sorted()
                .collect(Collectors.toList());

        if (deductList.size()==1&&deductList.get(0).getType().equals("1")){//说明是秒杀商品
            //走分布式锁
            Long pid=deductList.get(0).getProductId();
            Long quantity=deductList.get(0).getQuantity();
            String key="product:"+pid;
            Long result = (Long) redisFeignClient.execute(key, quantity.toString()).getData();
            if (result == -1) {
                throw new BusinessException("商品不存在");
            } else if (result == -2) {
                throw new BusinessException("商品库存不足");
            }
            logger.info("扣减成功，剩余库存: {}", result);
        }

        List<Product> productList=this.baseMapper.lockProducts(productIds); // 加锁

        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        deductList.forEach(stockDeductDTO -> {
            Long pid=stockDeductDTO.getProductId();
            Product product = productMap.get(pid);
            if (product == null) {
                throw new BusinessException("商品ID " + pid + " 不存在");
            }
            if (product.getStock() < stockDeductDTO.getQuantity()) {
                throw new BusinessException("商品" + product.getName() + "库存不够");
            }
        });


        this.baseMapper.batchDeductStock(deductList);
    }

    @Override
    public void updateLikeCount(int type,Long pid) {
        String key="like:"+pid;
        if (redisFeignClient.exists(key).getData()){
            redisFeignClient.increment(key,type);
            return ;
        }
        QueryWrapper<Product> wrapper=new QueryWrapper<>();
        wrapper.eq("id",pid).select("like_count");
        Long likeCount=this.getOne(wrapper).getLikeCount();
        redisFeignClient.setValue(key,likeCount);

        UpdateWrapper<Product> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",pid).setSql("like_count = like_count + "+type);
        this.update(updateWrapper);
    }

    @Override
    public void updateBatchLike(List<LikeUpdateDTO> dtoList) {
        List<Long> productIds = dtoList.stream()
                .map(LikeUpdateDTO::getPid)
                .sorted()
                .collect(Collectors.toList());

        this.baseMapper.lockProducts(productIds); // 加锁
        this.baseMapper.updateBatchLike(dtoList);
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
