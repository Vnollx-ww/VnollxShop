package com.example.common.model.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteCardItemDTO {
    private String keyword;
    private List<String> idList;
}
