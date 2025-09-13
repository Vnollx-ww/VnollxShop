package com.example.product.feign.middleware;

import com.example.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@FeignClient(name = "middleware-service",path = "/api/middleware/cache") // 这里只需要服务名
public interface RedisFeignClient {
    /**
     * 设置缓存值（无过期时间）
     */
    @PostMapping("/set")
    Result<Void> setValue(@RequestParam("key") String key, @RequestBody Object value);

    /**
     * 设置带过期时间的缓存值
     */
    @PostMapping("/setWithExpire")
    Result<Void> setValueWithExpire(
            @RequestParam("key") String key,
            @RequestBody Object value,
            @RequestParam("expireTime") long expireTime,
            @RequestParam(value = "timeUnit", defaultValue = "MILLISECONDS") TimeUnit timeUnit);

    /**
     * 获取缓存值
     */
    @GetMapping("/get")
    Result<Object> getValue(@RequestParam("key") String key);

    /**
     * 删除缓存
     */
    @DeleteMapping("/delete")
    Result<Void> deleteValue(@RequestParam("key") String key);

    /**
     * 设置键的过期时间
     */
    @PostMapping("/expire")
    Result<Void> setExpire(
            @RequestParam("key") String key,
            @RequestParam("expireTime") long expireTime,
            @RequestParam(value = "timeUnit", defaultValue = "MILLISECONDS") TimeUnit timeUnit);

    /**
     * 检查键是否存在
     */
    @GetMapping("/exists")
    Result<Boolean> exists(@RequestParam("key") String key);

    /**
     * 递增操作
     */
    @PostMapping("/increment")
    Result<Long> increment(
            @RequestParam("key") String key,
            @RequestParam(value = "delta", defaultValue = "1") long delta);

    /**
     * 递减操作
     */
    @PostMapping("/decrement")
    Result<Long> decrement(
            @RequestParam("key") String key,
            @RequestParam(value = "delta", defaultValue = "1") long delta);

    /**
     * 设置哈希值
     */
    @PostMapping("/hash/set")
    Result<String> setHashValue(
            @RequestParam("key") String key,
            @RequestParam("hashKey") String hashKey,
            @RequestBody Object value);

    /**
     * 获取哈希值
     */
    @GetMapping("/hash/get")
    Result<Object> getHashValue(
            @RequestParam("key") String key,
            @RequestParam("hashKey") String hashKey);

    /**
     * 删除哈希字段
     */
    @DeleteMapping("/hash/delete")
    Result<Void> deleteHashValue(
            @RequestParam("key") String key,
            @RequestParam("hashKey") String hashKey);

    /**
     * 检查哈希字段是否存在
     */
    @GetMapping("/hash/exists")
    Result<Boolean> hashExists(
            @RequestParam("key") String key,
            @RequestParam("hashKey") String hashKey);

    @PostMapping("/execute")
    Result<Object> execute(
            @RequestParam String key,
            @RequestParam String quantity
    );
}
