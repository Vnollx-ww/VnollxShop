package com.example.middleware.consumer;

import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.middleware.feign.OrderFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RocketMQMessageListener(
    topic = "${rocketmq.order.topic:order_topic}",
    consumerGroup = "${rocketmq.consumer.group:order_consumer_group}",
    awaitTerminationMillisWhenShutdown = 10000,    // 关闭时等待时间
    consumeTimeout = 15                            // 消费超时时间（分钟）
)
@RequiredArgsConstructor
public class OrderConsumer implements RocketMQListener<MessageExt> {
    

    private final OrderFeignClient orderFeignClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @Override
    public void onMessage(MessageExt message) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            CreateOrderDTO messageDTO = objectMapper.readValue(messageBody, CreateOrderDTO.class);
            
            logger.info("接收到订单创建消息: {}", message.getMsgId());
            
            // 处理订单创建
            orderFeignClient.insertOrder(messageDTO);
            
        } catch (Exception e) {
            logger.error("消息处理失败, MessageId: {}", message.getMsgId(), e);
            // 根据重试次数决定是否重试
            if (message.getReconsumeTimes() < 3) {
                throw new RuntimeException("需要重试");
            } else {
                logger.warn("消息重试超过3次, 丢弃消息: {}", message.getMsgId());
                // 记录到死信队列或人工处理
            }
        }
    }
}