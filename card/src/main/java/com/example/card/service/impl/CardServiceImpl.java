package com.example.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.card.entity.CardItem;
import com.example.card.mapper.CardItemMapper;
import com.example.card.service.CardService;
import com.example.common.exception.BusinessException;
import com.example.common.model.card.dto.AddCardItemDTO;
import com.example.common.model.card.dto.DeleteCardItemByShopDTO;
import com.example.common.model.card.dto.DeleteCardItemDTO;
import com.example.common.model.card.dto.UpdateNumberDTO;
import com.example.common.model.card.vo.CardItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl extends ServiceImpl<CardItemMapper,CardItem> implements CardService {
    @Override
    //@DS("slave")
    public List<CardItemVO> getCardItemList(Long uid) {
        QueryWrapper<CardItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        return this.list(queryWrapper).stream()
                .map(cardItem -> {
                    CardItemVO vo = new CardItemVO();
                    BeanUtils.copyProperties(cardItem, vo);
                    vo.setId(String.valueOf(cardItem.getId()));
                    vo.setPid(String.valueOf(cardItem.getPid()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    //@DS("slave")
    public Long getCardItemListCount(Long uid) {
        QueryWrapper<CardItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        return this.count();
    }

    @Override
    public void updateNumber(UpdateNumberDTO dto) {
        UpdateWrapper<CardItem> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",dto.getCiid()).setSql("number = "+dto.getNumber());
        this.update(updateWrapper);
    }

    @Override
    public void deleteCardItem(DeleteCardItemDTO dto) {
        QueryWrapper<CardItem> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotBlank(dto.getKeyword())){
            queryWrapper.like("product_name",dto.getKeyword());
        }
        List<Long> idList = dto.getIdList().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        queryWrapper.in("id",idList);
        this.baseMapper.delete(queryWrapper);
    }

    @Override
    public void deleteCardItemByShop(DeleteCardItemByShopDTO dto) {
        QueryWrapper<CardItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("uid",dto.getUid()).in("pid",dto.getPidList());
        this.baseMapper.delete(queryWrapper);
    }

    @Override
    public void addCardItem(AddCardItemDTO dto, Long uid) {
        QueryWrapper<CardItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("uid",uid).eq("pid",dto.getPid());
        if (this.count(queryWrapper)>0){
            throw new BusinessException("该商品已添加至购物车，请勿重复点击");
        }
        CardItem cardItem=new CardItem();
        cardItem.setCover(dto.getCover());
        cardItem.setUnitPrice(dto.getUnitPrice());
        cardItem.setPid(Long.parseLong(dto.getPid()));
        cardItem.setUid(uid);
        this.save(cardItem);
    }
}
