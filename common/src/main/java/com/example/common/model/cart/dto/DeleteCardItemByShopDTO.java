package com.example.common.model.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteCardItemByShopDTO {
    private List<Long> pidList;
    private Long uid;
    public DeleteCardItemByShopDTO(){}
    public DeleteCardItemByShopDTO(List<Long> pidList,Long uid){
        this.pidList=pidList;
        this.uid=uid;
    }
}
