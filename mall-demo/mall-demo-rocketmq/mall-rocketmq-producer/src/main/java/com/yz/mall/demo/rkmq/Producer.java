package com.yz.mall.demo.rkmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author yunze
 * @since 2025/9/7 16:53
 */
public class Producer {
    public static void main(String[] args) throws MQClientException, InterruptedException {
        // 初始化一个消息生产者
        DefaultMQProducer producer = new DefaultMQProducer("DemoProducer");
        // 指定nameserver地址
        producer.setNamesrvAddr("192.168.3.237:9876");
        // 启动消息生产者服务
        producer.start();
        for (int i = 0; i < 100; i++) {
            try {
                // 创建消息。消息由Topic,Tag和body三个属性组成，其中Body就是消息内容
                Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ" +i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                // 发送消息，获取发送结果
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        Thread.sleep(100000);
        // 消息发送完后，停止消息生产者服务。
        producer.shutdown();
    }
}
