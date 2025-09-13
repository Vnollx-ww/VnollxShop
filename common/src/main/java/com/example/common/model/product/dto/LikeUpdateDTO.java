package com.example.common.model.product.dto;

import lombok.Data;

@Data
public class LikeUpdateDTO {
    private Long likeCount;
    private Long pid;
    public LikeUpdateDTO(){}
    public LikeUpdateDTO(Long likeCount,Long pid){
        this.likeCount=likeCount;
        this.pid=pid;
    }
}
