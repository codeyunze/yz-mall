package com.yz.mall.gateway.config;

import com.yz.mall.gateway.kafka.GatewayAccessLogKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 网关 Kafka 配置：注册访问日志 Producer，并在启动后打印连接信息。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayKafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final GatewayAccessLogProperties accessLogProperties;

    /**
     * 访问日志 Kafka 生产者。依赖注入 KafkaTemplate，等自动配置完成后再创建。
     *
     * @param kafkaTemplate Spring Kafka 自动配置的模板
     */
    @Bean
    @ConditionalOnProperty(prefix = "yz.gateway.access-log.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GatewayAccessLogKafkaProducer gatewayAccessLogKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new GatewayAccessLogKafkaProducer(kafkaTemplate, accessLogProperties);
    }

    /**
     * 启动时由 KafkaAdmin 创建访问日志 Topic，避免 Broker 未开 auto.create.topics 时 UNKNOWN_TOPIC_OR_PARTITION。
     */
    @Bean
    @ConditionalOnProperty(prefix = "yz.gateway.access-log.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
    public NewTopic gatewayAccessLogTopic() {
        GatewayAccessLogProperties.Kafka kafka = accessLogProperties.getKafka();
        return TopicBuilder.name(kafka.getTopic()).partitions(kafka.getPartitions()).replicas(kafka.getReplicas()).build();
    }

    /**
     * 应用就绪后打印 Kafka bootstrap 与访问日志 Topic。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void logKafkaConfig() {
        log.info(">>>>>>>>>>> kafka config ready. bootstrapServers={} accessLogTopic={} kafkaEnabled={}",
                kafkaProperties.getBootstrapServers(),
                accessLogProperties.getKafka().getTopic(),
                accessLogProperties.getKafka().isEnabled());
    }
}
