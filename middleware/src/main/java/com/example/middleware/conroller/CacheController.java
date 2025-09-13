package com.example.middleware.conroller;

import com.example.common.result.Result;
import com.example.middleware.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/middleware/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService redisService;

    /**
     * 设置缓存值（无过期时间）
     */
    @PostMapping("/set")
    public Result<Void> setValue(@RequestParam String key, @RequestBody Object value) {
        redisService.set(key, value);
        return Result.success();
    }

    /**
     * 设置带过期时间的缓存值
     */
    @PostMapping("/setWithExpire")
    public Result<Void> setValueWithExpire(
            @RequestParam String key,
            @RequestBody Object value,
            @RequestParam long expireTime,
            @RequestParam(defaultValue = "MILLISECONDS") TimeUnit timeUnit) {

        redisService.set(key, value, timeUnit.toMillis(expireTime));
        return Result.success();
    }

    /**
     * 获取缓存值
     */
    @GetMapping("/get")
    public Result<Object> getValue(@RequestParam String key) {
        Object value = redisService.get(key);
        return Result.success(value);
    }

    /**
     * 删除缓存
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteValue(@RequestParam String key) {
        boolean result = redisService.delete(key);
        return Result.success();
    }

    /**
     * 设置键的过期时间
     */
    @PostMapping("/expire")
    public Result<Void> setExpire(
            @RequestParam String key,
            @RequestParam long expireTime,
            @RequestParam(defaultValue = "MILLISECONDS") TimeUnit timeUnit) {

        boolean result = redisService.expire(key, timeUnit.toMillis(expireTime));
        return Result.success();
    }

    /**
     * 检查键是否存在
     */
    @GetMapping("/exists")
    public Result<Boolean> exists(@RequestParam String key) {
        boolean exists = redisService.exists(key);
        return Result.success(exists);
    }

    /**
     * 递增操作
     */
    @PostMapping("/increment")
    public Result<Long> increment(
            @RequestParam String key,
            @RequestParam(defaultValue = "1") long delta) {

        Long result = redisService.increment(key, delta);
        return Result.success(result);
    }

    /**
     * 递减操作
     */
    @PostMapping("/decrement")
    public Result<Long> decrement(
            @RequestParam String key,
            @RequestParam(defaultValue = "1") long delta) {

        Long result = redisService.decrement(key, delta);
        return Result.success(result);
    }

    /**
     * 设置哈希值
     */
    @PostMapping("/hash/set")
    public Result<String> setHashValue(
            @RequestParam String key,
            @RequestParam String hashKey,
            @RequestBody Object value) {

        redisService.setHash(key, hashKey, value);
        return Result.success();
    }

    /**
     * 获取哈希值
     */
    @GetMapping("/hash/get")
    public Result<Object> getHashValue(
            @RequestParam String key,
            @RequestParam String hashKey) {

        Object value = redisService.getHash(key, hashKey);
        return Result.success(value);
    }

    /**
     * 删除哈希字段
     */
    @DeleteMapping("/hash/delete")
    public Result<Void> deleteHashValue(
            @RequestParam String key,
            @RequestParam String hashKey) {

        redisService.deleteHash(key, hashKey);
        return Result.success();
    }

    /**
     * 检查哈希字段是否存在
     */
    @GetMapping("/hash/exists")
    public Result<Boolean> hashExists(
            @RequestParam String key,
            @RequestParam String hashKey) {

        boolean exists = redisService.hasKey(key, hashKey);
        return Result.success(exists);
    }
    @PostMapping("/execute")
    public Result<Object> execute(
            @RequestParam String key,
            @RequestParam String quantity
    ) {
        return Result.success(redisService.execute(key,quantity));
    }
}