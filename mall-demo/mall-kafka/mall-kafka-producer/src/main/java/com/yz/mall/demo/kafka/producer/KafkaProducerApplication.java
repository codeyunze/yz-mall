package com.yz.mall.demo.kafka.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Kafka 生产者服务：通过 HTTP 接口发送消息到 Kafka。
 */
@SpringBootApplication
@EnableDiscoveryClient
public class KafkaProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args);
    }
}
