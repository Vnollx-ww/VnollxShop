package com.example.card.service;

import com.example.common.model.card.dto.AddCardItemDTO;
import com.example.common.model.card.dto.DeleteCardItemByShopDTO;
import com.example.common.model.card.dto.DeleteCardItemDTO;
import com.example.common.model.card.dto.UpdateNumberDTO;
import com.example.common.model.card.vo.CardItemVO;

import java.util.List;

public interface CardService {
    List<CardItemVO> getCardItemList(Long uid);
    Long getCardItemListCount(Long uid);
    void updateNumber(UpdateNumberDTO dto);
    void deleteCardItem(DeleteCardItemDTO dto);
    void deleteCardItemByShop(DeleteCardItemByShopDTO dto);
    void addCardItem(AddCardItemDTO dto, Long uid);
}
