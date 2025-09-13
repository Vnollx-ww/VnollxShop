package com.example.middleware.service;

public interface CacheService {
    void set(String key, Object value);
    void set(String key, Object value, long expireTime);
    Object get(String key);
    boolean delete(String key);
    boolean expire(String key, long expireTime);
    boolean exists(String key);
    Long increment(String key, long delta);
    Long decrement(String key, long delta);
    void setHash(String key, String hashKey, Object value);
    Object getHash(String key, String hashKey);
    void deleteHash(String key, String hashKey);
    boolean hasKey(String key, String hashKey);
}
