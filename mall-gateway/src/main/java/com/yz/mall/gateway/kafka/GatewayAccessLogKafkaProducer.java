package com.yz.mall.gateway.kafka;

import co.elastic.apm.api.ElasticApm;
import com.yz.mall.gateway.config.GatewayAccessLogProperties;
import com.yz.mall.json.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 异步投递网关访问日志到 Kafka，失败只打 warn，不影响请求转发。
 * <p>
 * 由 {@link com.yz.mall.gateway.config.GatewayKafkaConfig} 注册，避免 {@code @ConditionalOnBean(KafkaTemplate)}
 * 在组件扫描阶段因自动配置尚未完成而跳过 Bean。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Slf4j
@RequiredArgsConstructor
public class GatewayAccessLogKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final GatewayAccessLogProperties properties;

    /**
     * 按 path 分区异步发送；不阻塞 WebFlux 线程。
     *
     * @param message 已组装的访问日志
     */
    public void send(GatewayAccessLogMessage message) {
        if (message == null) {
            return;
        }
        String topic = properties.getKafka().getTopic();
        try {
            String payload = JacksonUtil.getObjectMapper().writeValueAsString(message);
            kafkaTemplate.send(topic, ElasticApm.currentTransaction().getTraceId(), payload).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("网关访问日志写入 Kafka 失败 topic={} path={}", topic, message.getPath(), ex);
                }
            });
        } catch (Exception e) {
            log.warn("网关访问日志序列化失败 path={}", message.getPath(), e);
        }
    }
}
