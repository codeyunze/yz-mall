package com.yz.auth.jms.consumer;

import com.rabbitmq.client.Channel;
import com.yz.common.Constants;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DirectConsumer {

    Integer stock = 20;

    @RabbitListener(queues = Constants.QUEUE_DIRECT)
    public void directReceive(Message message, Channel channel, String msg) throws IOException {
        minusStock(1, msg);
        basicAck(message, channel);
    }

    @RabbitListener(queues = Constants.QUEUE_DIRECT)
    public void directReceive2(Message message, Channel channel, String msg) throws IOException {
        minusStock(2, msg);
        basicAck(message, channel);
    }

    private void minusStock(Integer index, String msg){
        if (stock > 0) {
            try {
                Thread.sleep((int) (Math.random() * 500) + 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(Thread.currentThread().getName() + "----->consumer" + index + " : " + msg + "售卖第" + stock + "张票，库存剩余：" + (--stock));
        } else {
            log.info("库存不足：" + stock);
        }
    }

    private void basicAck(Message message, Channel channel) throws IOException {
        long deliveryTag = Long.parseLong(String.valueOf(message.getHeaders().get("amqp_deliveryTag")));
        channel.basicAck(deliveryTag, false);
    }
}
