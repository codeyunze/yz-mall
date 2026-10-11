package com.yz.mall.tw.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 遥测 Kafka 消费装配。
 * <p>
 * 仅当 {@code tw.telemetry.kafka.enabled=true} 时启用 {@code @KafkaListener}；
 * 默认关闭，避免无 Broker 时启动失败。
 */
@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "tw.telemetry.kafka", name = "enabled", havingValue = "true")
public class TwTelemetryKafkaConfig {
}
