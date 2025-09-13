package com.example.card.service;

import com.example.common.model.cart.dto.AddCardItemDTO;
import com.example.common.model.cart.dto.DeleteCardItemByShopDTO;
import com.example.common.model.cart.dto.DeleteCardItemDTO;
import com.example.common.model.cart.dto.UpdateNumberDTO;
import com.example.common.model.cart.vo.CartItemVO;

import java.util.List;

public interface CardService {
    List<CartItemVO> getCardItemList(Long uid);
    Long getCardItemListCount(Long uid);
    void updateNumber(UpdateNumberDTO dto);
    void deleteCardItem(DeleteCardItemDTO dto);
    void deleteCardItemByShop(DeleteCardItemByShopDTO dto);
    void addCardItem(AddCardItemDTO dto, Long uid);
}
