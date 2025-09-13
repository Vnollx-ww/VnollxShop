package com.example.common.model.user.dto;

import lombok.Data;

@Data
public class UpdateBalanceDTO {
    private Long uid;
    private Double cost;
    public UpdateBalanceDTO(){}
    public UpdateBalanceDTO(Long uid,Double cost){
        this.uid=uid;
        this.cost=cost;
    }
}
