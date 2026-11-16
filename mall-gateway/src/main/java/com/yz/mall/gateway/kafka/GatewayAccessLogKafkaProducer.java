package com.yz.mall.gateway.kafka;

import com.yz.mall.base.HeaderConstants;
import com.yz.mall.gateway.config.GatewayAccessLogProperties;
import com.yz.mall.json.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;

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
     * 异步发送访问日志，不阻塞 WebFlux 线程；{@code traceId} 写入 Kafka header {@code x-trace-id}。
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
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, TraceContext.traceId(), payload);
            String traceId = message.getTraceId();
            if (traceId != null && !traceId.isBlank()) {
                record.headers().add(HeaderConstants.TRACE_ID_HEADER, traceId.getBytes(StandardCharsets.UTF_8));
            }
            kafkaTemplate.send(record).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("网关访问日志写入 Kafka 失败 topic={} path={}", topic, message.getPath(), ex);
                }
            });
        } catch (Exception e) {
            log.warn("网关访问日志序列化失败 path={}", message.getPath(), e);
        }
    }
}
