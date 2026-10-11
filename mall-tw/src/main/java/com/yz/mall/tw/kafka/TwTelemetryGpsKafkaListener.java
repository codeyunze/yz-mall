package com.yz.mall.tw.kafka;

import com.yz.mall.tw.service.TwTelemetryIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 消费 {@code tw-telemetry-raw}，写入 Latest + 轨迹缓冲。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "tw.telemetry.kafka", name = "enabled", havingValue = "true")
public class TwTelemetryGpsKafkaListener {

    private final TwTelemetryIngestService ingestService;

    /**
     * 遥测原始 GPS 消费入口。
     *
     * @param record Kafka 记录（Key 建议为 VIN）
     */
    @KafkaListener(topics = "${tw.telemetry.kafka.topic}", groupId = "${tw.telemetry.kafka.group-id}")
    public void onMessage(ConsumerRecord<String, String> record) {
        if (record == null) {
            return;
        }
        String key = record.key();
        String value = record.value();
        try {
            boolean ok = ingestService.handleRaw(value, key);
            if (!ok) {
                log.debug("遥测消息未入库 topic={} partition={} offset={}", record.topic(), record.partition(), record.offset());
            }
        } catch (Exception ex) {
            // 不抛出，避免反复重试毒消息堵死分区；后续可接死信
            log.error("遥测消费异常 topic={} offset={}: {}", record.topic(), record.offset(), ex.getMessage(), ex);
        }
    }
}
