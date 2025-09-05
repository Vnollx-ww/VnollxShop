package com.example.common.model.data.dto;

import lombok.Data;

@Data
public class RedisSetKeyDTO {
    private String key;
    private String value;
    private Long expire;
    public RedisSetKeyDTO(String key,String value,Long expire){
        this.key=key;
        this.value=value;
        this.expire=expire;
    }
}
