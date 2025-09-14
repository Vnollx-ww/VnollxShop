package com.example.middleware.config;

import lombok.Getter;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name-server}")
    private String nameSrvAddr;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Getter
    @Value("${rocketmq.order.topic}")
    private String orderTopic;

    private static final Logger logger = LoggerFactory.getLogger(RocketMQConfig.class);

    @Bean
    public DefaultMQProducer mqProducer(){
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameSrvAddr);
        // 设置重试次数
        producer.setRetryTimesWhenSendFailed(3);
        try {
            producer.start();
            logger.info("RocketMQ生产者启动成功");
        } catch (MQClientException e) {
            logger.error("RocketMQ生产者启动失败: ", e);
            throw new RuntimeException("RocketMQ生产者启动失败", e);
        }
        return producer;
    }

}