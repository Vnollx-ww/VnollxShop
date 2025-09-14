package com.example.middleware.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.order.dto.CreateOrderDTO;
import com.example.middleware.config.RocketMQConfig;
import com.example.middleware.service.MqService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MqServiceImpl implements MqService {
    private static final Logger logger = LoggerFactory.getLogger(MqServiceImpl.class);
    private final DefaultMQProducer mqProducer;

    private final RocketMQConfig rocketMQConfig;
    private final ObjectMapper objectMapper;

    @Override
    public void createOrderMessage(CreateOrderDTO dto) {
        try {
            String messageBody = objectMapper.writeValueAsString(dto);

            Message message = new Message(
                    rocketMQConfig.getOrderTopic(),
                    "ORDER_CREATE",
                    messageBody.getBytes(StandardCharsets.UTF_8)
            );

            // 发送消息
            SendResult sendResult = mqProducer.send(message);
            logger.info("订单消息发送成功: {}", sendResult);

        } catch (Exception e) {
            logger.error("订单消息发送失败: ", e);
            throw new BusinessException("订单创建失败，请重试");
        }
    }
}
