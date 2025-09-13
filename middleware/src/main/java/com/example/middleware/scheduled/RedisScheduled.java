package com.example.middleware.scheduled;

import com.example.common.model.product.dto.LikeUpdateDTO;
import com.example.middleware.feign.ProductFeignClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisScheduled {
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductFeignClient productFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(RedisScheduled.class);
    private static final String LIKE_KEY_PREFIX = "like:";
    @Scheduled(fixedRate = 60000) // 固定延迟1分钟（60000毫秒）
    public void scheduleUpdateLikeCount() {
        logger.info("开始执行点赞数同步定时任务...");
        try {
            List<LikeUpdateDTO> updateList = new ArrayList<>();

            ScanOptions options = ScanOptions.scanOptions()
                    .match(LIKE_KEY_PREFIX + "*")
                    .count(100) // 每次扫描100个
                    .build();

            // 使用SCAN迭代获取所有匹配的键
            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    String key = cursor.next();
                    // 提取ID（去掉"like:"前缀）
                    Long pid = Long.parseLong(key.substring(LIKE_KEY_PREFIX.length()));

                    // 获取该键对应的点赞数值
                    String likeCountStr = redisTemplate.opsForValue().get(key);
                    if (likeCountStr != null) {
                        try {
                            Long likeCount = Long.parseLong(likeCountStr);
                            updateList.add(new LikeUpdateDTO(likeCount, pid));
                        } catch (NumberFormatException e) {
                            logger.warn("键 {} 的值不是有效的整数: {}", key, likeCountStr);
                        }
                    }
                }
            }

            // 如果有数据需要更新，则调用Feign客户端
            if (!updateList.isEmpty()) {
                productFeignClient.updateBatchLike(updateList);
                logger.info("点赞数同步定时任务执行完毕。共处理 {} 条数据。", updateList.size());
            } else {
                logger.info("没有需要同步的点赞数据。");
            }

        } catch (Exception e) {
            logger.error("点赞数同步任务执行失败: ", e);
        }
    }
}
