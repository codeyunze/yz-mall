package com.yz.mall.tw.access.service;

import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.dto.MqttPublishDto;
import com.yz.mall.tw.access.mqtt.EmqxPahoMqttClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 经 Eclipse Paho 下行发布到 EMQX
 */
@Service
@RequiredArgsConstructor
public class MqttPublishService {

    private final TwAccessProperties properties;
    private final EmqxPahoMqttClient emqxPahoMqttClient;

    /**
     * 发布 MQTT 消息。
     *
     * @param dto 入参
     */
    public void publish(MqttPublishDto dto) {
        String topic = dto.getTopic();
        if (topic == null || topic.isBlank()) {
            if (dto.getVin() == null || dto.getVin().isBlank()) {
                throw new BusinessException("请指定 topic 或 vin");
            }
            String suffix = dto.getSuffix() == null || dto.getSuffix().isBlank() ? "down/cmd" : dto.getSuffix();
            topic = properties.getTopicPrefix() + "/" + dto.getVin().trim().toUpperCase() + "/" + suffix.replaceFirst("^/", "");
        }
        emqxPahoMqttClient.publish(topic, dto.getPayload(), dto.getQos());
    }
}
