package com.yz.mall.tw.service;

/**
 * 遥测原始消息接入：解析 → 校验/去噪 → Redis+MySQL → CH 缓冲
 */
public interface TwTelemetryIngestService {

    /**
     * 处理一条原始 GPS JSON（Kafka value）；非法坐标或去噪丢弃时打 warn 并返回 false。
     *
     * @param payload 消息体 JSON
     * @param kafkaKey Kafka Key（通常为 VIN，可补全 body 缺省 vin）
     * @return true 表示 latest 已写入；false 表示丢弃
     */
    boolean handleRaw(String payload, String kafkaKey);
}
