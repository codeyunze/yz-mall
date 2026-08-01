package com.yz.mall.demo.kafka.common;

/**
 * Kafka Topic 与消费者组常量。
 */
public final class KafkaTopics {

    private KafkaTopics() {}

    /** 订单事件 Kafka Topic */
    public static final String ORDER_TOPIC = "mall-kafka-order-topic";

    /** 消费者组（微服务 consumer 模块） */
    public static final String ORDER_CONSUMER_GROUP = "mall-kafka-order-group";

    /** 消费者组（demo 一体化模块） */
    public static final String DEMO_CONSUMER_GROUP = "mall-kafka-demo-group";
}
