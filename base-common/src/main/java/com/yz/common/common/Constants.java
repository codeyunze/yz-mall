package com.yz.common.common;

/**
 * @author : yunze
 * @date : 2022/6/27 23:36
 */
public class Constants {

    /**
     * direct模式直接发送消息到队列的队列名称
     */
    public static final String QUEUE_DIRECT = "bc-server-message-common";

    /**
     * fanout模式发送到交换机，交换机再发送到所绑定的所有队列里的交换机名称
     */
    public static final String EXCHANGE_FANOUT = "bc.server.exchange.fanout";

    public static final String EXCHANGE_FANOUT_QUEUE_1 = "bc-server-exchange-fanout-queue-1";

    public static final String EXCHANGE_FANOUT_QUEUE_2 = "bc-server-exchange-fanout-queue-2";

    /**
     * fanout模式基础之下，试用header进行消息转发
     */
    public static final String EXCHANGE_FANOUT_HEADER = "bc.server.exchange.fanout.header";

    public static final String EXCHANGE_FANOUT_HEADER_QUEUE_1 = "bc-server-exchange-fanout-header-queue-1";

    public static final String EXCHANGE_FANOUT_HEADER_QUEUE_2 = "bc-server-exchange-fanout-header-queue-2";

    public static final String EXCHANGE_FANOUT_HEADER_QUEUE_3 = "bc-server-exchange-fanout-header-queue-3";

    /**
     * topic模式下，消息发送到交换机，交换机会往routingKey匹配上的队列转发消息
     */
    public static final String EXCHANGE_TOPIC = "bc.server.exchange.topic";

    public static final String EXCHANGE_TOPIC_QUEUE_1 = "bc-server-exchange-topic-queue-1";

    public static final String EXCHANGE_TOPIC_QUEUE_2 = "bc-server-exchange-topic-queue-2";

    public static final String EXCHANGE_TOPIC_QUEUE_3 = "bc-server-exchange-topic-queue-3";

    /**
     * 死信队列
     */
    public static final String DL_EXCHANGE = "bc.server.exchange.dl";

    public static final String DL_QUEUE = "bc-server-queue-dl";

    public static final String DL_KEY = "bc.server.key.dl";
}
