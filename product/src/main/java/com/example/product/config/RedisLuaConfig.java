package com.example.product.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisLuaConfig {

    private DefaultRedisScript<Long> deductStockScript;

    @PostConstruct
    public void init() {
        deductStockScript = new DefaultRedisScript<>();
        deductStockScript.setScriptText(
                "local key = KEYS[1] " +
                        "local quantity = tonumber(ARGV[1]) " +
                        "local productInfo = redis.call('GET', key) " +
                        "if not productInfo then " +
                        "return -1 " +  // -1 表示商品不存在
                        "end " +
                        "local json = cjson.decode(productInfo) " +
                        "local stock = tonumber(json['stock']) " +
                        "if stock < quantity then " +
                        "return -2 " +  // -2 表示库存不足
                        "end " +
                        "json['stock'] = stock - quantity " +
                        "local updatedProductInfo = cjson.encode(json) " +
                        "redis.call('SET', key, updatedProductInfo) " +
                        "return tonumber(json['stock']) "  // 返回剩余库存
        );
        deductStockScript.setResultType(Long.class);
    }

}
