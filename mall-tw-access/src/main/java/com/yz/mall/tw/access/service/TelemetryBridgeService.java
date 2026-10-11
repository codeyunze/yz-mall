package com.yz.mall.tw.access.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.dto.EmqxGpsBridgeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 上行 GPS 桥接：归一化报文后写入 Kafka，供 mall-tw 消费落库。
 * <p>
 * 入口兼容：
 * <ul>
 * <li>Paho 订阅 {@code {topicPrefix}/+/up/gps}</li>
 * <li>EMQX Rule HTTP {@code POST /emqx/bridge/gps}</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryBridgeService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TwAccessProperties properties;
    private final DeviceSessionCache sessionCache;
    private final ObjectMapper objectMapper = JacksonUtil.getObjectMapper();

    /**
     * 桥接一条 GPS 上报到 Kafka（默认 Topic {@code tw-telemetry-raw}）。
     * <p>
     * 要求 payload 含 {@code lng}/{@code lat}；缺 {@code vin} 时按 Topic / 会话缓存补齐。Kafka Key 为 VIN（大写），便于按车分区。
     *
     * @param request 上行包装（Topic、clientId、payload 等）
     * @return {@code true} 已投递；开关关闭、缺字段或异常时为 {@code false}
     */
    public boolean bridgeGps(EmqxGpsBridgeRequest request) {
        if (!properties.getKafka().isEnabled()) {
            log.warn("Kafka 桥接未启用，丢弃 GPS");
            return false;
        }
        if (request == null) {
            return false;
        }
        try {
            String vin = resolveVin(request);
            JsonNode gpsNode = resolvePayload(request, vin);
            // mall-tw 侧最低要求：经纬度必填
            if (gpsNode == null || !gpsNode.has("lng") || !gpsNode.has("lat")) {
                log.warn("GPS 桥接丢弃：缺少 lng/lat topic={}", request.getTopic());
                return false;
            }
            if (!(gpsNode instanceof ObjectNode objectNode)) {
                log.warn("GPS 桥接丢弃：payload 非对象");
                return false;
            }
            // payload 未带 vin 时用解析结果回填，避免下游按 VIN 路由失败
            if (!objectNode.hasNonNull("vin") && vin != null) {
                objectNode.put("vin", vin);
            }
            String key = objectNode.path("vin").asText(vin == null ? "" : vin).toUpperCase();
            String value = objectMapper.writeValueAsString(objectNode);
            kafkaTemplate.send(properties.getKafka().getTelemetryTopic(), key, value);
            log.info("GPS 已投递 Kafka topic={} key={}", properties.getKafka().getTelemetryTopic(), key);
            return true;
        } catch (Exception ex) {
            log.error("GPS 桥接 Kafka 失败: {}", ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * 解析 VIN：显式字段 → Topic 第二段 → 鉴权会话缓存（clientId / username）。
     *
     * @param request 上行请求
     * @return 大写 VIN；无法解析时 {@code null}
     */
    private String resolveVin(EmqxGpsBridgeRequest request) {
        if (request.getVin() != null && !request.getVin().isBlank()) {
            return request.getVin().trim().toUpperCase();
        }
        String fromTopic = extractVinFromTopic(request.getTopic());
        if (fromTopic != null) {
            return fromTopic;
        }
        String vin = sessionCache.getVin(request.getClientid());
        if (vin != null) {
            return vin;
        }
        return sessionCache.getVin(request.getUsername());
    }

    /**
     * 从 Topic 提取 VIN，约定 {@code {topicPrefix}/{vin}/up/gps}。
     *
     * @param topic MQTT Topic
     * @return VIN；前缀不匹配或段不足时 {@code null}
     */
    private String extractVinFromTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return null;
        }
        // titan/{vin}/up/gps（前缀见 tw.access.topic-prefix）
        String[] parts = topic.split("/");
        if (parts.length >= 2 && properties.getTopicPrefix().equals(parts[0])) {
            return parts[1].toUpperCase();
        }
        return null;
    }

    /**
     * 兼容多种 payload 形态：JsonNode / 字符串 / 根节点展平的 lng+lat。
     *
     * @param request 上行请求
     * @param vin     已解析 VIN（展平形态时回填）
     * @return GPS JSON 对象；无法构造时 {@code null}
     */
    private JsonNode resolvePayload(EmqxGpsBridgeRequest request, String vin) throws Exception {
        if (request.getPayload() != null && !request.getPayload().isNull()) {
            // Rule 偶发把 JSON 当字符串塞进 payload
            if (request.getPayload().isTextual()) {
                return objectMapper.readTree(request.getPayload().asText());
            }
            return request.getPayload();
        }
        if (request.getPayload_str() != null && !request.getPayload_str().isBlank()) {
            return objectMapper.readTree(request.getPayload_str());
        }
        // 部分 Rule 模板把 lng/lat 提到根节点
        if (request.getLng() != null && request.getLat() != null) {
            ObjectNode node = objectMapper.createObjectNode();
            if (vin != null) {
                node.put("vin", vin);
            }
            node.set("lng", objectMapper.valueToTree(request.getLng()));
            node.set("lat", objectMapper.valueToTree(request.getLat()));
            return node;
        }
        return null;
    }
}
