package com.example.card.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.card.entity.CardItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CardItemMapper extends BaseMapper<CardItem> {
}
