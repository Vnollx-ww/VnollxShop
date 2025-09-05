package com.example.common.model.product.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.Triple;

import java.util.List;

@Data
public class ShopDTO {
    private List<Triple<Long, Long, Double>> itemList;//pid number price
    private Double totalCost;
}
