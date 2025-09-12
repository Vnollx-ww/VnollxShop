package com.example.product.service;

public interface ProductLikeService {
    void addLike(Long uid, Long pid);
    Boolean judgeIsLike(Long uid,Long pid);
    void cancelLike(Long uid,Long pid);
}
