package com.yz.search.jms.consumer;

import com.rabbitmq.client.Channel;
import com.yz.common.common.Constants;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 直发模式，直接将消息发送到队列里
 * 1. 不需要交换机
 * 2. 当一个队列有多个消费者时，一个消息只会由一个消费者消费（竞争的消费者模式）
 * 3. 默认是轮询，即会将消息轮流发给多个消费者（测试的是每个消费者发送5次），对消费比较慢的消费者不公平
 * @author : yunze
 * @date : 2022/6/27 23:41
 */
@Component
public class DirectConsumer {

    @RabbitListener(queues = Constants.QUEUE_DIRECT)
    public void directReceive(Message message, Channel channel, String msg) throws IOException {
        System.out.println("consumer1 directReceive message : " + msg);
        basicAck(message, channel);
    }

    @RabbitListener(queues = Constants.QUEUE_DIRECT)
    public void directReceive2(Message message, Channel channel, String msg) throws IOException {
        System.out.println("consumer2 directReceive message : " + msg);
        basicAck(message, channel);
    }

    private void basicAck(Message message, Channel channel) throws IOException {
        long deliveryTag = Long.parseLong(String.valueOf(message.getHeaders().get("amqp_deliveryTag")));
        channel.basicAck(deliveryTag, false);
    }
}
